package com.alnaifer.nehb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Halaqa - Groupe dynamique de mémorisation du Coran
 * Regroupe les élèves par niveau de compétence, indépendamment de l'âge
 */
@Entity
@Table(name = "halaqas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Halaqa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "target_juz")
    private Integer targetJuz;

    @Column(name = "target_sura")
    private String targetSura;

    @Column(name = "skill_level")
    private String skillLevel; // Débutant, Intermédiaire, Avancé

    @Column(name = "max_capacity")
    private Integer maxCapacity = 15;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TeacherProfile teacher;

    @ManyToMany(mappedBy = "halaqas")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<StudentProfile> students = new ArrayList<>();

    @Column(name = "meeting_days")
    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "halaqa_meeting_days", joinColumns = @JoinColumn(name = "halaqa_id"))
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> meetingDays = new ArrayList<>();

    @Column(name = "meeting_time")
    private LocalTime meetingTime;

    @Column(name = "room_assignment")
    private String roomAssignment;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "halaqa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CourseSession> courseSessions = new ArrayList<>();

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
