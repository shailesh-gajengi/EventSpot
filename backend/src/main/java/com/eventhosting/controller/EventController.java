package com.eventhosting.controller;

import com.eventhosting.dto.request.EventCreateRequest;
import com.eventhosting.dto.response.ApiResponse;
import com.eventhosting.dto.response.EventResponse;
import com.eventhosting.service.HostingEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final HostingEventService hostingEventService;

    public EventController(HostingEventService hostingEventService) {
        this.hostingEventService = hostingEventService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> submitEvent(@Valid @RequestBody EventCreateRequest request) {
        EventResponse response = hostingEventService.submitEvent(request);
        return new ResponseEntity<>(ApiResponse.success("Event submitted successfully for admin review", response), HttpStatus.CREATED);
    }

    @GetMapping("/hosted")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getHostedEvents() {
        List<EventResponse> events = hostingEventService.getHostedEvents();
        return ResponseEntity.ok(ApiResponse.success("Hosted events retrieved successfully", events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable Long id) {
        EventResponse event = hostingEventService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success("Event retrieved successfully", event));
    }
}
