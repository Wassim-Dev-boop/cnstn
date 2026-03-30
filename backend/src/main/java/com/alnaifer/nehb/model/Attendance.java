package com.alnaifer.nehb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Présence - Enregistrement des absences/présences aux sessions
 */
@Entity
@Table(name = "attendances", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"course_session_id", "student_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_session_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CourseSession courseSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private StudentProfile student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status; // PRESENT, ABSENT, LATE, EXCUSED

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "absence_reason")
    private String absenceReason;

    @Column(name = "justification_document")
    private String justificationDocument;

    @Column(name = "is_justified", nullable = false)
    private boolean isJustified = false;

    @Column(name = "justified_by")
    private String justifiedBy; // Nom du parent qui a justifié

    @Column(name = "justified_at")
    private LocalDateTime justifiedAt;

    @Column(name = "notified_parent", nullable = false)
    private boolean notifiedParent = false;

    @Column(name = "parent_notification_sent_at")
    private LocalDateTime parentNotificationSentAt;

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
}
