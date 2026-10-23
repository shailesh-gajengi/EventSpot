package com.eventhosting.service;

import com.eventhosting.dto.request.LoginRequest;
import com.eventhosting.dto.request.OrganizerRegisterRequest;
import com.eventhosting.dto.response.AuthResponse;
import com.eventhosting.dto.response.OrganizerResponse;

public interface OrganizerService {

    OrganizerResponse register(OrganizerRegisterRequest request);

    AuthResponse login(LoginRequest request);

    OrganizerResponse getOrganizerById(Long organizerId);
}
