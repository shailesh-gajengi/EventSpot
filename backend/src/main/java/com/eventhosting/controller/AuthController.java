package com.eventhosting.controller;

import com.eventhosting.dto.request.LoginRequest;
import com.eventhosting.dto.request.OrganizerRegisterRequest;
import com.eventhosting.dto.response.ApiResponse;
import com.eventhosting.dto.response.AuthResponse;
import com.eventhosting.dto.response.OrganizerResponse;
import com.eventhosting.service.OrganizerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OrganizerService organizerService;

    public AuthController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<OrganizerResponse>> register(@Valid @RequestBody OrganizerRegisterRequest request) {
        OrganizerResponse response = organizerService.register(request);
        return new ResponseEntity<>(ApiResponse.success("Organizer registered successfully", response), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = organizerService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
