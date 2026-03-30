package com.alnaifer.nehb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Profil Parent/Tuteur - Permet de lier un parent à ses enfants
 */
@Entity
@Table(name = "parent_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "relationship_type")
    private String relationshipType; // Père, Mère, Tuteur légal

    @Column(name = "is_primary_contact", nullable = false)
    private boolean isPrimaryContact = true;

    @OneToMany(mappedBy = "parentProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<StudentProfile> children = new ArrayList<>();

    @Column(name = "receive_absence_notifications", nullable = false)
    private Boolean receiveAbsenceNotifications = true;

    @Column(name = "receive_progress_notifications", nullable = false)
    private Boolean receiveProgressNotifications = true;

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
