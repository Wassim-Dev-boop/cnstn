package com.alnaifer.nehb.repository;

import com.alnaifer.nehb.model.HifzProgress;
import com.alnaifer.nehb.model.HifzStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository pour la gestion des progressions Hifz
 */
@Repository
public interface HifzProgressRepository extends JpaRepository<HifzProgress, String> {

    /**
     * Trouver toutes les progressions d'un élève
     */
    List<HifzProgress> findByStudentIdOrderByJuzNumberAscAyatStartAsc(String studentId);

    /**
     * Trouver les Sabaqs en attente d'évaluation
     */
    List<HifzProgress> findByStatusAndAssignmentDateBefore(HifzStatus status, LocalDate date);

    /**
     * Trouver les Sabqis dans la fenêtre glissante de 15 jours
     */
    List<HifzProgress> findByStudentIdAndStatusAndValidationDateBetween(
            String studentId,
            HifzStatus status,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Trouver les Manzils à réviser avant une date donnée
     */
    List<HifzProgress> findByStudentIdAndStatusAndNextReviewDateBefore(
            String studentId,
            HifzStatus status,
            LocalDate date
    );

    /**
     * Trouver les progressions nécessitant une consolidation
     */
    List<HifzProgress> findByNeedsConsolidationTrueOrderByNextReviewDateAsc();

    /**
     * Compter les portions validées par élève
     */
    long countByStudentIdAndStatus(String studentId, HifzStatus status);

    /**
     * Trouver les progressions par Juz et élève
     */
    List<HifzProgress> findByStudentIdAndJuzNumber(String studentId, Integer juzNumber);

    /**
     * Requête personnalisée pour obtenir les statistiques de progression
     */
    @Query("SELECT hp.juzNumber, AVG(hp.averageQualityScore) " +
           "FROM HifzProgress hp " +
           "WHERE hp.student.id = :studentId " +
           "GROUP BY hp.juzNumber")
    List<Object[]> getProgressStatsByJuz(@Param("studentId") String studentId);
}
