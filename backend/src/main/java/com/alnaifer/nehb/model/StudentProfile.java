package com.alnaifer.nehb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Profil Élève (Talib) - Informations spécifiques aux étudiants
 */
@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "grade_level")
    private String gradeLevel;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "current_juz")
    private Integer currentJuz;

    @Column(name = "current_sura")
    private String currentSura;

    @Column(name = "memorization_level")
    private String memorizationLevel;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<HifzProgress> hifzProgressList = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "student_classes",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SchoolClass> classes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "student_halaqas",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "halaqa_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Halaqa> halaqas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_profile_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ParentProfile parentProfile;

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
