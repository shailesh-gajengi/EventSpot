package com.eventhosting.controller;

import com.eventhosting.dto.response.ApiResponse;
import com.eventhosting.dto.response.EventResponse;
import com.eventhosting.dto.response.OrganizerResponse;
import com.eventhosting.service.HostingEventService;
import com.eventhosting.service.OrganizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizers")
public class OrganizerController {

    private final OrganizerService organizerService;
    private final HostingEventService hostingEventService;

    public OrganizerController(OrganizerService organizerService, HostingEventService hostingEventService) {
        this.organizerService = organizerService;
        this.hostingEventService = hostingEventService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizerResponse>> getOrganizerProfile(@PathVariable Long id) {
        OrganizerResponse response = organizerService.getOrganizerById(id);
        return ResponseEntity.ok(ApiResponse.success("Organizer profile retrieved", response));
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getEventsByOrganizer(@PathVariable Long id) {
        List<EventResponse> events = hostingEventService.getEventsByOrganizer(id);
        return ResponseEntity.ok(ApiResponse.success("Artist events retrieved", events));
    }
}
