package com.alnaifer.nehb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Progression Hifz - Suivi de la mémorisation du Coran par élève
 * Implémente le cycle Sabaq / Sabqi / Manzil
 */
@Entity
@Table(name = "hifz_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HifzProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private StudentProfile student;

    @Column(name = "juz_number", nullable = false)
    private Integer juzNumber;

    @Column(name = "sura_name", nullable = false)
    private String suraName;

    @Column(name = "ayat_start", nullable = false)
    private Integer ayatStart;

    @Column(name = "ayat_end", nullable = false)
    private Integer ayatEnd;

    @Column(name = "total_ayat")
    private Integer totalAyat;

    /**
     * État actuel selon la machine à états finis du cahier des charges
     * SABAQ: Nouvelle leçon en cours d'apprentissage
     * SABQI: Révision récente (15 derniers jours)
     * MANZIL: Révision ancienne (rétention long terme)
     * VALIDATED: Portion complètement mémorisée
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HifzStatus status;

    @Column(name = "assignment_date")
    private LocalDate assignmentDate;

    @Column(name = "first_evaluation_date")
    private LocalDate firstEvaluationDate;

    @Column(name = "last_evaluation_date")
    private LocalDate lastEvaluationDate;

    @Column(name = "validation_date")
    private LocalDate validationDate;

    @Column(name = "current_quality_score")
    private Double currentQualityScore;

    @Column(name = "average_quality_score")
    private Double averageQualityScore;

    @Column(name = "total_evaluations")
    private Integer totalEvaluations = 0;

    @Column(name = "successful_evaluations")
    private Integer successfulEvaluations = 0;

    @Column(name = "needs_consolidation", nullable = false)
    private boolean needsConsolidation = false;

    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Calcule le taux de réussite
     */
    public double getSuccessRate() {
        if (totalEvaluations == 0) {
            return 0.0;
        }
        return (double) successfulEvaluations / totalEvaluations * 100;
    }
}
