package com.eventhosting.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OrganizerRegisterRequest {

    @NotBlank(message = "Organizer name is required")
    @Size(max = 100, message = "Organizer name cannot exceed 100 characters")
    private String organizerName;

    @NotBlank(message = "Contact number is required")
    @Size(max = 15, message = "Contact cannot exceed 15 characters")
    private String organizerContact;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String organizerEmail;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Terms consent must be provided")
    @AssertTrue(message = "You must agree to the Terms and Conditions")
    private Boolean termsConsent;

    public OrganizerRegisterRequest() {
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
}
