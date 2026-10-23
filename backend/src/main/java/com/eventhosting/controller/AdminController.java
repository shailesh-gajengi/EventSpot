package com.eventhosting.controller;

import com.eventhosting.dto.request.ApprovalActionRequest;
import com.eventhosting.dto.response.ApiResponse;
import com.eventhosting.dto.response.ApprovalResponse;
import com.eventhosting.dto.response.DashboardStatsResponse;
import com.eventhosting.dto.response.EventResponse;
import com.eventhosting.service.ApprovalService;
import com.eventhosting.service.HostingEventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final HostingEventService hostingEventService;
    private final ApprovalService approvalService;

    public AdminController(HostingEventService hostingEventService, ApprovalService approvalService) {
        this.hostingEventService = hostingEventService;
        this.approvalService = approvalService;
    }

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> getAllEvents(@RequestParam(required = false) String status) {
        List<EventResponse> events = hostingEventService.getAllEvents(status);
        return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
    }

    @PutMapping("/events/{eventId}/approval")
    public ResponseEntity<ApiResponse<ApprovalResponse>> processEventApproval(
            @PathVariable Long eventId,
            @Valid @RequestBody ApprovalActionRequest request) {
        ApprovalResponse response = approvalService.processApproval(eventId, request);
        String action = "Approved".equalsIgnoreCase(request.getStatus()) ? "approved and hosted" : "rejected";
        return ResponseEntity.ok(ApiResponse.success("Event successfully " + action, response));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse stats = approvalService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics retrieved", stats));
    }

    @GetMapping("/events/{eventId}/approval")
    public ResponseEntity<ApiResponse<ApprovalResponse>> getApprovalRecord(@PathVariable Long eventId) {
        ApprovalResponse response = approvalService.getApprovalByEventId(eventId);
        return ResponseEntity.ok(ApiResponse.success("Approval record retrieved", response));
    }
}
