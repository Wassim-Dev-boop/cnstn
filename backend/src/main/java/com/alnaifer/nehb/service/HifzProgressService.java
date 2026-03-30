package com.alnaifer.nehb.service;

import com.alnaifer.nehb.model.*;
import com.alnaifer.nehb.repository.HifzProgressRepository;
import com.alnaifer.nehb.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service de gestion de la progression Hifz
 * Implémente la logique du cycle Sabaq / Sabqi / Manzil
 */
@Service
@RequiredArgsConstructor
public class HifzProgressService {

    private final HifzProgressRepository hifzProgressRepository;
    private final StudentRepository studentRepository;

    /**
     * Assigner un nouveau Sabaq à un élève
     * Vérifie d'abord que le Sabqi est suffisant pour progresser
     */
    @Transactional
    public HifzProgress assignNewSabaq(String studentId, Integer juzNumber, 
                                        String suraName, Integer ayatStart, Integer ayatEnd) {
        StudentProfile student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Élève non trouvé"));

        // Vérifier si l'élève peut recevoir un nouveau Sabaq
        if (!canAssignNewSabaq(studentId)) {
            throw new RuntimeException("Le niveau Sabqi de l'élève est insuffisant. Consolidation requise.");
        }

        HifzProgress progress = HifzProgress.builder()
                .student(student)
                .juzNumber(juzNumber)
                .suraName(suraName)
                .ayatStart(ayatStart)
                .ayatEnd(ayatEnd)
                .totalAyat(ayatEnd - ayatStart + 1)
                .status(HifzStatus.SABAQ)
                .assignmentDate(LocalDate.now())
                .totalEvaluations(0)
                .successfulEvaluations(0)
                .needsConsolidation(false)
                .build();

        return hifzProgressRepository.save(progress);
    }

    /**
     * Obtenir toute la progression d'un élève
     */
    @Transactional(readOnly = true)
    public List<HifzProgress> getStudentProgress(String studentId) {
        return hifzProgressRepository.findByStudentIdOrderByJuzNumberAscAyatStartAsc(studentId);
    }

    /**
     * Obtenir les Sabaqs en attente d'évaluation
     */
    @Transactional(readOnly = true)
    public List<HifzProgress> getPendingEvaluations(LocalDate date) {
        return hifzProgressRepository.findByStatusAndAssignmentDateBefore(
                HifzStatus.SABAQ, 
                date.plusDays(1)
        );
    }

    /**
     * Obtenir les Sabqis à réviser (fenêtre glissante 15 jours)
     * Selon la règle du cahier des charges
     */
    @Transactional(readOnly = true)
    public List<HifzProgress> getSabqiRevision(String studentId) {
        LocalDate fifteenDaysAgo = LocalDate.now().minusDays(15);
        return hifzProgressRepository.findByStudentIdAndStatusAndValidationDateBetween(
                studentId,
                HifzStatus.SABQI,
                fifteenDaysAgo,
                LocalDate.now()
        );
    }

    /**
     * Obtenir les Manzils planifiés pour révision (SRS - Répétition Espacée)
     * Utilise la courbe de l'oubli d'Ebbinghaus
     */
    @Transactional(readOnly = true)
    public List<HifzProgress> getManzilRevision(String studentId, LocalDate date) {
        return hifzProgressRepository.findByStudentIdAndStatusAndNextReviewDateBefore(
                studentId,
                HifzStatus.MANZIL,
                date
        );
    }

    /**
     * Calculer la progression par Juz (pour les jauges circulaires du dashboard)
     */
    @Transactional(readOnly = true)
    public Map<Integer, Double> getProgressByJuz(String studentId) {
        List<HifzProgress> progresses = getStudentProgress(studentId);
        
        return progresses.stream()
                .collect(Collectors.groupingBy(
                        HifzProgress::getJuzNumber,
                        Collectors.averagingDouble(p -> {
                            if (p.getStatus() == HifzStatus.VALIDATED) {
                                return 100.0;
                            } else if (p.getAverageQualityScore() != null) {
                                return p.getAverageQualityScore();
                            }
                            return 0.0;
                        })
                ));
    }

    /**
     * Vérifier si un élève peut recevoir un nouveau Sabaq
     * Règle : Le système bloque si l'évaluation globale du Sabqi < seuil critique (70%)
     */
    @Transactional(readOnly = true)
    public boolean canAssignNewSabaq(String studentId) {
        List<HifzProgress> sabqiList = getSabqiRevision(studentId);
        
        if (sabqiList.isEmpty()) {
            // Pas de Sabqi en cours, on peut assigner
            return true;
        }

        double averageScore = sabqiList.stream()
                .filter(p -> p.getAverageQualityScore() != null)
                .mapToDouble(HifzProgress::getAverageQualityScore)
                .average()
                .orElse(100.0);

        // Seuil critique à 70% selon le cahier des charges
        return averageScore >= 70.0;
    }

    /**
     * Mettre à jour le statut d'une portion après évaluation
     * Machine à états finis : SABAQ -> SABQI -> MANZIL -> VALIDATED
     */
    @Transactional
    public void updateStatusAfterEvaluation(HifzProgress progress, String grade) {
        HifzStatus currentStatus = progress.getStatus();
        
        if (currentStatus == HifzStatus.SABAQ) {
            // Trigger : Si Note >= B, le statut passe à "Sabqi"
            if ("A".equals(grade) || "B".equals(grade)) {
                progress.setStatus(HifzStatus.SABQI);
                progress.setFirstEvaluationDate(LocalDate.now());
                
                // Planifier la première révision Sabqi dans 3 jours
                progress.setNextReviewDate(LocalDate.now().plusDays(3));
            } else {
                // Note < B, le Sabaq est maintenu pour le lendemain
                progress.setNeedsConsolidation(true);
                progress.setNextReviewDate(LocalDate.now().plusDays(1));
            }
        } else if (currentStatus == HifzStatus.SABQI) {
            // Après plusieurs évaluations réussies en Sabqi, passer à Manzil
            if (progress.getSuccessfulEvaluations() >= 5) {
                progress.setStatus(HifzStatus.MANZIL);
                
                // Planifier la révision Manzil selon la courbe d'Ebbinghaus
                progress.setNextReviewDate(calculateEbbinghausReviewDate(progress));
            }
        } else if (currentStatus == HifzStatus.MANZIL) {
            // Après plusieurs révisions Manzil réussies, valider
            if (progress.getSuccessfulEvaluations() >= 10) {
                progress.setStatus(HifzStatus.VALIDATED);
                progress.setValidationDate(LocalDate.now());
            }
        }

        progress.setLastEvaluationDate(LocalDate.now());
        hifzProgressRepository.save(progress);
    }

    /**
     * Calcul de la prochaine date de révision selon la courbe de l'oubli d'Ebbinghaus
     * Jours : 1, 3, 7, 14, 30, 60, 90...
     */
    private LocalDate calculateEbbinghausReviewDate(HifzProgress progress) {
        int reviewCount = progress.getTotalEvaluations();
        
        int[] ebbinghausDays = {1, 3, 7, 14, 30, 60, 90, 120, 180};
        
        int daysToAdd;
        if (reviewCount < ebbinghausDays.length) {
            daysToAdd = ebbinghausDays[reviewCount];
        } else {
            // Après tous les intervalles standards, doubler le dernier intervalle
            daysToAdd = ebbinghausDays[ebbinghausDays.length - 1] * 2;
        }

        return LocalDate.now().plusDays(daysToAdd);
    }

    /**
     * Marquer une portion comme nécessitant une consolidation urgente
     */
    @Transactional
    public void markForConsolidation(String progressId) {
        HifzProgress progress = hifzProgressRepository.findById(progressId)
                .orElseThrow(() -> new RuntimeException("Progression non trouvée"));
        
        progress.setNeedsConsolidation(true);
        progress.setNextReviewDate(LocalDate.now().plusDays(1));
        
        if (progress.getStatus() == HifzStatus.SABQI || progress.getStatus() == HifzStatus.MANZIL) {
            progress.setStatus(HifzStatus.SABAQ);
        }
        
        hifzProgressRepository.save(progress);
    }
}
