package com.eventhosting.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "approval",
    indexes = {
        @Index(name = "idx_approval_event_id", columnList = "event_id"),
        @Index(name = "idx_approval_organizer_id", columnList = "organizer_id"),
        @Index(name = "idx_approval_status", columnList = "approval_status")
    }
)
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long approvalId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private HostingEvent hostingEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private Organizer organizer;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 20)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "notification_sent", nullable = false)
    private Boolean notificationSent = false;

    public Approval() {
    }

    public Approval(HostingEvent hostingEvent, Organizer organizer) {
        this.hostingEvent = hostingEvent;
        this.organizer = organizer;
        this.approvalStatus = ApprovalStatus.PENDING;
        this.approvalDate = LocalDateTime.now();
        this.notificationSent = false;
    }

    @PrePersist
    public void prePersist() {
        if (this.approvalDate == null) {
            this.approvalDate = LocalDateTime.now();
        }
        if (this.approvalStatus == null) {
            this.approvalStatus = ApprovalStatus.PENDING;
        }
        if (this.notificationSent == null) {
            this.notificationSent = false;
        }
    }

    // Getters and Setters
    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public HostingEvent getHostingEvent() {
        return hostingEvent;
    }

    public void setHostingEvent(HostingEvent hostingEvent) {
        this.hostingEvent = hostingEvent;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Boolean getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(Boolean notificationSent) {
        this.notificationSent = notificationSent;
    }
}
