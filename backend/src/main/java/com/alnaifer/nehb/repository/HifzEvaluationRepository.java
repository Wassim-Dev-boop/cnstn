package com.alnaifer.nehb.repository;

import com.alnaifer.nehb.model.HifzEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des évaluations Hifz
 */
@Repository
public interface HifzEvaluationRepository extends JpaRepository<HifzEvaluation, String> {

    /**
     * Trouver toutes les évaluations d'une progression
     */
    List<HifzEvaluation> findByProgressIdOrderByEvaluationDateDesc(String progressId);

    /**
     * Trouver toutes les évaluations d'un élève
     */
    @Query("SELECT he FROM HifzEvaluation he " +
           "JOIN he.progress p " +
           "WHERE p.student.id = :studentId " +
           "ORDER BY he.evaluationDate DESC")
    List<HifzEvaluation> findByStudentId(@Param("studentId") String studentId);

    /**
     * Trouver les dernières évaluations d'un élève (limité)
     */
    @Query("SELECT he FROM HifzEvaluation he " +
           "JOIN he.progress p " +
           "WHERE p.student.id = :studentId " +
           "ORDER BY he.evaluationDate DESC")
    List<HifzEvaluation> findTopByStudentIdOrderByEvaluationDateDesc(
            @Param("studentId") String studentId, 
            int limit
    );

    /**
     * Compter les évaluations par élève
     */
    long countByProgressStudentId(String studentId);

    /**
     * Trouver les évaluations avec une note spécifique
     */
    List<HifzEvaluation> findByProgressStudentIdAndGrade(String studentId, String grade);
}
