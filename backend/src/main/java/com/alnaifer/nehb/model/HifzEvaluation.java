package com.alnaifer.nehb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
    @JoinColumn(name = "hifz_progress_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private HifzProgress progress;

    /**
     * Note globale (A, B, C, D selon le cahier des charges)
     */
    @Column(nullable = false)
    private String grade;

    /**
     * Score de qualité global (0-100)
     */
    @Column(name = "quality_score")
    private Double qualityScore;

    /**
     * Erreurs Tajwid stockées en JSON
     * Structure: { "lahnJali": [...], "lahnKhafi": [...] }
     */
    @Column(name = "tajwid_errors", columnDefinition = "jsonb")
    private Map<String, Object> tajwidErrors = new HashMap<>();

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
}
