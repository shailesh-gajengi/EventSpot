package com.eventhosting.service.impl;

import com.eventhosting.dto.request.ApprovalActionRequest;
import com.eventhosting.dto.response.ApprovalResponse;
import com.eventhosting.dto.response.DashboardStatsResponse;
import com.eventhosting.entity.Approval;
import com.eventhosting.entity.ApprovalStatus;
import com.eventhosting.entity.HostingEvent;
import com.eventhosting.entity.Organizer;
import com.eventhosting.event.EventStatusChangedNotificationEvent;
import com.eventhosting.exception.ResourceNotFoundException;
import com.eventhosting.repository.ApprovalRepository;
import com.eventhosting.repository.HostingEventRepository;
import com.eventhosting.repository.OrganizerRepository;
import com.eventhosting.service.ApprovalService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final HostingEventRepository hostingEventRepository;
    private final OrganizerRepository organizerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ApprovalServiceImpl(ApprovalRepository approvalRepository,
                               HostingEventRepository hostingEventRepository,
                               OrganizerRepository organizerRepository,
                               ApplicationEventPublisher eventPublisher) {
        this.approvalRepository = approvalRepository;
        this.hostingEventRepository = hostingEventRepository;
        this.organizerRepository = organizerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ApprovalResponse processApproval(Long eventId, ApprovalActionRequest request) {
        HostingEvent event = hostingEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        ApprovalStatus oldStatus = event.getApprovalStatus();
        ApprovalStatus newStatus = ApprovalStatus.fromString(request.getStatus().trim());

        event.setApprovalStatus(newStatus);
        if (newStatus == ApprovalStatus.REJECTED) {
            event.setRejectionReason(request.getRejectionReason());
        } else {
            event.setRejectionReason(null);
        }
        hostingEventRepository.save(event);

        Approval approval = approvalRepository.findByHostingEvent_EventId(eventId)
                .orElseGet(() -> {
                    Approval newApp = new Approval();
                    newApp.setHostingEvent(event);
                    newApp.setOrganizer(event.getOrganizer());
                    return newApp;
                });

        approval.setApprovalStatus(newStatus);
        approval.setApprovalDate(LocalDateTime.now());
        approval.setNotificationSent(true);

        if (newStatus == ApprovalStatus.REJECTED) {
            approval.setRejectionReason(request.getRejectionReason());
        } else {
            approval.setRejectionReason(null);
        }

        Approval savedApproval = approvalRepository.save(approval);

        // Publish status notification only if status has actually changed
        if (oldStatus != newStatus && (newStatus == ApprovalStatus.APPROVED || newStatus == ApprovalStatus.REJECTED)) {
            Organizer organizer = event.getOrganizer();
            if (organizer != null && organizer.getOrganizerEmail() != null && !organizer.getOrganizerEmail().isBlank()) {
                eventPublisher.publishEvent(new EventStatusChangedNotificationEvent(
                        organizer.getOrganizerEmail().trim(),
                        organizer.getOrganizerName(),
                        event.getEventName(),
                        oldStatus,
                        newStatus,
                        request.getRejectionReason()
                ));
            }
        }

        return mapToResponse(savedApproval, event);
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalResponse getApprovalByEventId(Long eventId) {
        Approval approval = approvalRepository.findByHostingEvent_EventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval record not found for event id: " + eventId));
        return mapToResponse(approval, approval.getHostingEvent());
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long totalEvents = hostingEventRepository.count();
        long pending = hostingEventRepository.countByApprovalStatus(ApprovalStatus.PENDING);
        long approved = hostingEventRepository.countByApprovalStatus(ApprovalStatus.APPROVED);
        long rejected = hostingEventRepository.countByApprovalStatus(ApprovalStatus.REJECTED);
        long totalOrganizers = organizerRepository.count();

        return new DashboardStatsResponse(totalEvents, pending, approved, rejected, totalOrganizers);
    }

    private ApprovalResponse mapToResponse(Approval approval, HostingEvent event) {
        ApprovalResponse res = new ApprovalResponse();
        res.setApprovalId(approval.getApprovalId());
        res.setEventId(event != null ? event.getEventId() : null);
        res.setEventName(event != null ? event.getEventName() : null);
        if (approval.getOrganizer() != null) {
            res.setOrganizerId(approval.getOrganizer().getOrganizerId());
            res.setOrganizerName(approval.getOrganizer().getOrganizerName());
        }
        res.setApprovalStatus(approval.getApprovalStatus() != null ? approval.getApprovalStatus().getDisplayValue() : "Pending");
        res.setRejectionReason(approval.getRejectionReason());
        res.setApprovalDate(approval.getApprovalDate());
        res.setNotificationSent(approval.getNotificationSent());
        return res;
    }
}
