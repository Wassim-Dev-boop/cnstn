package com.alnaifer.nehb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Profil Enseignant (Ustad) - Informations spécifiques aux professeurs
 */
@Entity
@Table(name = "teacher_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "max_students_per_class")
    private Integer maxStudentsPerClass = 20;

    @Column(name = "preferred_morning_hours")
    private Boolean preferredMorningHours = true;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Halaqa> halaqasLed = new ArrayList<>();

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CourseSession> courseSessions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "teacher_classes",
        joinColumns = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SchoolClass> classesTaught = new ArrayList<>();

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
