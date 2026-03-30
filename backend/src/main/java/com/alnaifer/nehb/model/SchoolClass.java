package com.alnaifer.nehb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe scolaire - Regroupement statique par âge ou cycle pour les matières générales
 */
@Entity
@Table(name = "school_classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "academic_year", nullable = false)
    private String academicYear; // Ex: "2024-2025"

    @Column(name = "grade_level")
    private String gradeLevel;

    @Column(name = "max_capacity")
    private Integer maxCapacity = 30;

    @Column(name = "room_assignment")
    private String roomAssignment;

    @ManyToMany(mappedBy = "classes")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<StudentProfile> students = new ArrayList<>();

    @ManyToMany(mappedBy = "classesTaught")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<TeacherProfile> teachers = new ArrayList<>();

    @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CourseSession> courseSessions = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

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
