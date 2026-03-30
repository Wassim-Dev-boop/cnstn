package com.alnaifer.nehb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Évaluation Hifz - Enregistrement des évaluations de mémorisation
 * Inclut l'évaluation tridimensionnelle du Tajwid
 */
@Entity
@Table(name = "hifz_evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HifzEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_session_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CourseSession courseSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hifz_progress_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private HifzProgress hifzProgress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TeacherProfile evaluator;

    /**
     * Note globale (A, B, C, D selon le cahier des charges)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Grade grade;

    /**
     * Score de qualité global (0-100)
     */
    @Column(name = "quality_score")
    private Double qualityScore;

    // === Évaluation Tridimensionnelle du Tajwid ===

    /**
     * Lahn Jali - Erreurs majeures (changement de lettre ou voyelle)
     */
    @Column(name = "lahn_jali_count")
    private Integer lahnJaliCount = 0;

    /**
     * Lahn Khafi - Erreurs mineures (Ghunna, Idgham, Madd, Ikhfa)
     */
    @Column(name = "lahn_khafi_count")
    private Integer lahnKhafiCount = 0;

    /**
     * Nombre d'erreurs sur Ghunna spécifiquement
     */
    @Column(name = "ghunna_errors")
    private Integer ghunnaErrors = 0;

    /**
     * Nombre d'erreurs sur Idgham
     */
    @Column(name = "idgham_errors")
    private Integer idghamErrors = 0;

    /**
     * Nombre d'erreurs sur Madd
     */
    @Column(name = "madd_errors")
    private Integer maddErrors = 0;

    /**
     * Nombre d'erreurs sur Ikhfa
     */
    @Column(name = "ikhfa_errors")
    private Integer ikhfaErrors = 0;

    /**
     * Nombre d'erreurs sur Noon Sakinah
     */
    @Column(name = "noon_sakinah_errors")
    private Integer noonSakinahErrors = 0;

    /**
     * Fluidité de la récitation (0-10)
     */
    @Column(name = "fluency_score")
    private Integer fluencyScore = 0;

    /**
     * Prononciation (Makharij) (0-10)
     */
    @Column(name = "pronunciation_score")
    private Integer pronunciationScore = 0;

    @Column(name = "evaluation_date", nullable = false)
    private LocalDate evaluationDate;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "teacher_comments")
    private String teacherComments;

    @Column(name = "audio_recording_url")
    private String audioRecordingUrl;

    @Column(name = "requires_remediation", nullable = false)
    private boolean requiresRemediation = false;

    @Column(name = "remediation_notes")
    private String remediationNotes;

    @Column(name = "parent_notified", nullable = false)
    private boolean parentNotified = false;

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
     * Calcule le score de qualité basé sur les erreurs de Tajwid
     */
    public void calculateQualityScore() {
        int totalErrors = lahnJaliCount * 3 + // Erreurs majeures pèsent plus lourd
                         lahnKhafiCount +
                         ghunnaErrors +
                         idghamErrors +
                         maddErrors +
                         ikhfaErrors +
                         noonSakinahErrors;

        // Score de base 100, moins les pénalités
        double baseScore = 100.0 - (totalErrors * 5);

        // Ajout des scores de fluidité et prononciation (pondérés)
        double fluencyBonus = (fluencyScore != null ? fluencyScore : 0) * 2;
        double pronunciationBonus = (pronunciationScore != null ? pronunciationScore : 0) * 2;

        this.qualityScore = Math.max(0, Math.min(100, baseScore + fluencyBonus + pronunciationBonus));
    }
}
