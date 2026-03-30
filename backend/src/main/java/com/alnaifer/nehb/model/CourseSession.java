package com.alnaifer.nehb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Session de cours - Représente un créneau horaire spécifique
 * Peut être lié à une Classe ou à une Halaqa
 */
@Entity
@Table(name = "course_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TeacherProfile teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "halaqa_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Halaqa halaqa;

    @Column(name = "room_assignment", nullable = false)
    private String roomAssignment;

    @Column(name = "google_meet_url")
    private String googleMeetUrl;

    @Column(name = "google_meet_space_id")
    private String googleMeetSpaceId;

    @Column(name = "is_online", nullable = false)
    private boolean isOnline = false;

    @Column(name = "is_hybrid", nullable = false)
    private boolean isHybrid = false;

    @Column(name = "session_type")
    private String sessionType; // Cours, Examen, Révision

    @OneToMany(mappedBy = "courseSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Attendance> attendances = new ArrayList<>();

    @OneToMany(mappedBy = "courseSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<HifzEvaluation> hifzEvaluations = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

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
     * Vérifie si cette session est liée à une Halaqa (enseignement coranique)
     */
    public boolean isHalaqaSession() {
        return halaqa != null;
    }

    /**
     * Vérifie si cette session est liée à une Classe (enseignement général)
     */
    public boolean isClassSession() {
        return schoolClass != null;
    }
}
