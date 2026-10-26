package com.eventhosting.controller;

import com.eventhosting.dto.request.LoginRequest;
import com.eventhosting.dto.request.OrganizerRegisterRequest;
import com.eventhosting.dto.response.AuthResponse;
import com.eventhosting.dto.response.OrganizerResponse;
import com.eventhosting.dto.response.ResponseBean;
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
    public ResponseEntity<ResponseBean<OrganizerResponse>> register(@Valid @RequestBody OrganizerRegisterRequest request) {
        OrganizerResponse response = organizerService.register(request);
        return new ResponseEntity<>(ResponseBean.success("Organizer registered successfully", response), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseBean<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = organizerService.login(request);
        return ResponseEntity.ok(ResponseBean.success("Login successful", response));
    }
}
