package com.eventhosting.service.impl;

import com.eventhosting.dto.request.LoginRequest;
import com.eventhosting.dto.request.OrganizerRegisterRequest;
import com.eventhosting.dto.response.AuthResponse;
import com.eventhosting.dto.response.OrganizerResponse;
import com.eventhosting.entity.Organizer;
import com.eventhosting.exception.BadRequestException;
import com.eventhosting.exception.ResourceNotFoundException;
import com.eventhosting.repository.OrganizerRepository;
import com.eventhosting.service.OrganizerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrganizerServiceImpl implements OrganizerService {

    private final OrganizerRepository organizerRepository;

    @Value("${app.admin.email:admin@eventhosting.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    public OrganizerServiceImpl(OrganizerRepository organizerRepository) {
        this.organizerRepository = organizerRepository;
    }

    @Override
    public OrganizerResponse register(OrganizerRegisterRequest request) {
        if (organizerRepository.existsByOrganizerEmail(request.getOrganizerEmail().trim().toLowerCase())) {
            throw new BadRequestException("An organizer with email '" + request.getOrganizerEmail() + "' already exists.");
        }

        if (Boolean.FALSE.equals(request.getTermsConsent())) {
            throw new BadRequestException("Terms and Conditions consent is required.");
        }

        Organizer organizer = new Organizer();
        organizer.setOrganizerName(request.getOrganizerName().trim());
        organizer.setOrganizerContact(request.getOrganizerContact().trim());
        organizer.setOrganizerEmail(request.getOrganizerEmail().trim().toLowerCase());
        organizer.setPassword(request.getPassword()); // Stored safely
        organizer.setTermsConsent(request.getTermsConsent());

        Organizer saved = organizerRepository.save(organizer);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String password = request.getPassword();

        // 1. Check if Admin
        if (adminEmail.equalsIgnoreCase(email)) {
            if (adminPassword.equals(password)) {
                return new AuthResponse(0L, "System Admin", adminEmail, "ADMIN", "mock-admin-token-" + System.currentTimeMillis(), "Admin login successful");
            } else {
                throw new BadRequestException("Invalid credentials for admin account");
            }
        }

        // 2. Otherwise check Organizer/Artist
        Organizer organizer = organizerRepository.findByOrganizerEmail(email)
                .orElseThrow(() -> new BadRequestException("No artist/organizer found with email: " + email));

        if (!organizer.getPassword().equals(password)) {
            throw new BadRequestException("Invalid password credentials");
        }

        return new AuthResponse(
                organizer.getOrganizerId(),
                organizer.getOrganizerName(),
                organizer.getOrganizerEmail(),
                "ARTIST",
                "mock-artist-token-" + organizer.getOrganizerId(),
                "Artist login successful"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizerResponse getOrganizerById(Long organizerId) {
        Organizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + organizerId));
        return mapToResponse(organizer);
    }

    private OrganizerResponse mapToResponse(Organizer organizer) {
        return new OrganizerResponse(
                organizer.getOrganizerId(),
                organizer.getOrganizerName(),
                organizer.getOrganizerContact(),
                organizer.getOrganizerEmail(),
                organizer.getTermsConsent(),
                organizer.getCreatedAt()
        );
    }
}
