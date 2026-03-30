package com.alnaifer.nehb.service;

import com.alnaifer.nehb.model.HifzEvaluation;
import com.alnaifer.nehb.model.HifzProgress;
import com.alnaifer.nehb.repository.HifzEvaluationRepository;
import com.alnaifer.nehb.repository.HifzProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de gestion des évaluations Hifz
 * Implémente l'évaluation tridimensionnelle du Tajwid
 */
@Service
@RequiredArgsConstructor
public class HifzEvaluationService {

    private final HifzEvaluationRepository hifzEvaluationRepository;
    private final HifzProgressRepository hifzProgressRepository;
    private final HifzProgressService hifzProgressService;

    /**
     * Évaluer un Sabaq avec les erreurs Tajwid
     * @param progressId ID de la progression
     * @param grade Note (A, B, C, D)
     * @param tajwidErrors Carte des erreurs Tajwid (Lahn Jali / Lahn Khafi)
     */
    @Transactional
    public HifzEvaluation evaluateSabaq(String progressId, String grade, 
                                         Map<String, Object> tajwidErrors) {
        HifzProgress progress = hifzProgressRepository.findById(progressId)
                .orElseThrow(() -> new RuntimeException("Progression non trouvée"));

        // Calculer le score de qualité basé sur les erreurs
        double qualityScore = calculateQualityScore(grade, tajwidErrors);

        HifzEvaluation evaluation = HifzEvaluation.builder()
                .progress(progress)
                .grade(grade)
                .qualityScore(qualityScore)
                .tajwidErrors(tajwidErrors)
                .evaluationDate(LocalDate.now())
                .build();

        hifzEvaluationRepository.save(evaluation);

        // Mettre à jour la progression
        updateProgressStats(progress, qualityScore, grade);

        // Mettre à jour le statut selon la machine à états finis
        hifzProgressService.updateStatusAfterEvaluation(progress, grade);

        return evaluation;
    }

    /**
     * Calculer le score de qualité basé sur la note et les erreurs Tajwid
     * Score de 0 à 100
     */
    private double calculateQualityScore(String grade, Map<String, Object> tajwidErrors) {
        // Score de base selon la note
        double baseScore;
        switch (grade.toUpperCase()) {
            case "A": baseScore = 95.0; break;
            case "B": baseScore = 80.0; break;
            case "C": baseScore = 65.0; break;
            case "D": baseScore = 50.0; break;
            default: baseScore = 70.0;
        }

        // Déduire des points pour chaque erreur Tajwid
        double penalty = 0.0;

        // Lahn Jali (erreurs majeures) - 5 points par erreur
        if (tajwidErrors.containsKey("lahnJali")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> majorErrors = (List<Map<String, Object>>) tajwidErrors.get("lahnJali");
            penalty += majorErrors.size() * 5.0;
        }

        // Lahn Khafi (erreurs mineures) - 2 points par erreur
        if (tajwidErrors.containsKey("lahnKhafi")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> minorErrors = (List<Map<String, Object>>) tajwidErrors.get("lahnKhafi");
            penalty += minorErrors.size() * 2.0;
        }

        return Math.max(0.0, baseScore - penalty);
    }

    /**
     * Mettre à jour les statistiques de la progression
     */
    private void updateProgressStats(HifzProgress progress, double qualityScore, String grade) {
        int totalEvals = progress.getTotalEvaluations() + 1;
        progress.setTotalEvaluations(totalEvals);

        // Incrémenter les évaluations réussies si note >= B
        if ("A".equals(grade) || "B".equals(grade)) {
            progress.setSuccessfulEvaluations(progress.getSuccessfulEvaluations() + 1);
        }

        // Mettre à jour le score actuel
        progress.setCurrentQualityScore(qualityScore);

        // Calculer la moyenne
        double totalScore = (progress.getAverageQualityScore() != null ? 
                            progress.getAverageQualityScore() * (totalEvals - 1) : 0) + qualityScore;
        progress.setAverageQualityScore(totalScore / totalEvals);
    }

    /**
     * Obtenir l'historique des évaluations d'une progression
     */
    @Transactional(readOnly = true)
    public List<HifzEvaluation> getEvaluationHistory(String progressId) {
        return hifzEvaluationRepository.findByProgressIdOrderByEvaluationDateDesc(progressId);
    }

    /**
     * Analyser les erreurs Tajwid pour générer un radar chart
     * Retourne les faiblesses par règle de Tajwid
     */
    @Transactional(readOnly = true)
    public Map<String, Double> getTajwidErrorAnalysis(String studentId) {
        List<HifzEvaluation> evaluations = hifzEvaluationRepository.findByStudentId(studentId);

        Map<String, Integer> errorCounts = new HashMap<>();
        Map<String, Double> radarData = new HashMap<>();

        // Initialiser toutes les règles de Tajwid à 0
        String[] tajwidRules = {
            "Makharij", "Ghunna", "Idgham", "Ikhfa", "Iqlab",
            "Madd", "Qalqalah", "Raa", "NoonSakinah", "MeemSakinah"
        };

        for (String rule : tajwidRules) {
            errorCounts.put(rule, 0);
        }

        // Compter les erreurs par règle
        for (HifzEvaluation eval : evaluations) {
            if (eval.getTajwidErrors() != null) {
                analyzeErrors(eval.getTajwidErrors(), errorCounts);
            }
        }

        // Convertir en scores (moins d'erreurs = score plus élevé)
        int totalEvals = Math.max(1, evaluations.size());
        for (String rule : tajwidRules) {
            int errors = errorCounts.get(rule);
            // Score inverse : 100 si aucune erreur, diminue avec le nombre d'erreurs
            double score = Math.max(0, 100 - (errors * 5));
            radarData.put(rule, score);
        }

        return radarData;
    }

    /**
     * Analyser récursivement les erreurs Tajwid
     */
    @SuppressWarnings("unchecked")
    private void analyzeErrors(Map<String, Object> errors, Map<String, Integer> errorCounts) {
        for (Map.Entry<String, Object> entry : errors.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof List) {
                List<Map<String, Object>> errorList = (List<Map<String, Object>>) value;
                for (Map<String, Object> error : errorList) {
                    if (error.containsKey("rule")) {
                        String rule = (String) error.get("rule");
                        errorCounts.merge(rule, 1, Integer::sum);
                    }
                }
            } else if (value instanceof Map) {
                analyzeErrors((Map<String, Object>) value, errorCounts);
            }
        }
    }

    /**
     * Obtenir les évaluations récentes d'un élève
     */
    @Transactional(readOnly = true)
    public List<HifzEvaluation> getRecentEvaluations(String studentId, int limit) {
        return hifzEvaluationRepository.findTopByStudentIdOrderByEvaluationDateDesc(studentId, limit);
    }
}
