package com.eventhosting.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizer")
public class Organizer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organizer_id")
    private Long organizerId;

    @Column(name = "organizer_name", nullable = false, length = 100)
    private String organizerName;

    @Column(name = "organizer_contact", nullable = false, length = 15)
    private String organizerContact;

    @Column(name = "organizer_email", nullable = false, unique = true, length = 100)
    private String organizerEmail;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "terms_consent", nullable = false)
    private Boolean termsConsent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HostingEvent> events = new ArrayList<>();

    public Organizer() {
    }

    public Organizer(String organizerName, String organizerContact, String organizerEmail, String password, Boolean termsConsent) {
        this.organizerName = organizerName;
        this.organizerContact = organizerContact;
        this.organizerEmail = organizerEmail;
        this.password = password;
        this.termsConsent = termsConsent;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.termsConsent == null) {
            this.termsConsent = false;
        }
    }

    // Getters and Setters
    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getOrganizerContact() {
        return organizerContact;
    }

    public void setOrganizerContact(String organizerContact) {
        this.organizerContact = organizerContact;
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public void setOrganizerEmail(String organizerEmail) {
        this.organizerEmail = organizerEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getTermsConsent() {
        return termsConsent;
    }

    public void setTermsConsent(Boolean termsConsent) {
        this.termsConsent = termsConsent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<HostingEvent> getEvents() {
        return events;
    }

    public void setEvents(List<HostingEvent> events) {
        this.events = events;
    }
}
