package com.eventhosting.dto.response;

import java.time.LocalDateTime;

public class OrganizerResponse {

    private Long organizerId;
    private String organizerName;
    private String organizerContact;
    private String organizerEmail;
    private Boolean termsConsent;
    private LocalDateTime createdAt;

    public OrganizerResponse() {
    }

    public OrganizerResponse(Long organizerId, String organizerName, String organizerContact, String organizerEmail, Boolean termsConsent, LocalDateTime createdAt) {
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.organizerContact = organizerContact;
        this.organizerEmail = organizerEmail;
        this.termsConsent = termsConsent;
        this.createdAt = createdAt;
    }

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
}
