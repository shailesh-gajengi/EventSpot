package com.eventhosting.service.impl;

import com.eventhosting.dto.request.EventCreateRequest;
import com.eventhosting.dto.response.EventResponse;
import com.eventhosting.entity.Approval;
import com.eventhosting.entity.ApprovalStatus;
import com.eventhosting.entity.HostingEvent;
import com.eventhosting.entity.Organizer;
import com.eventhosting.event.EventSubmittedNotificationEvent;
import com.eventhosting.exception.ResourceNotFoundException;
import com.eventhosting.repository.ApprovalRepository;
import com.eventhosting.repository.HostingEventRepository;
import com.eventhosting.repository.OrganizerRepository;
import com.eventhosting.service.HostingEventService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HostingEventServiceImpl implements HostingEventService {

    private final HostingEventRepository hostingEventRepository;
    private final OrganizerRepository organizerRepository;
    private final ApprovalRepository approvalRepository;
    private final ApplicationEventPublisher eventPublisher;

    public HostingEventServiceImpl(HostingEventRepository hostingEventRepository,
                                   OrganizerRepository organizerRepository,
                                   ApprovalRepository approvalRepository,
                                   ApplicationEventPublisher eventPublisher) {
        this.hostingEventRepository = hostingEventRepository;
        this.organizerRepository = organizerRepository;
        this.approvalRepository = approvalRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public EventResponse submitEvent(EventCreateRequest request) {
        Organizer organizer = organizerRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found with id: " + request.getOrganizerId()));

        HostingEvent event = new HostingEvent();
        event.setOrganizer(organizer);
        event.setEventName(request.getEventName().trim());
        event.setEventType(request.getEventType().trim());
        event.setEventGenre(request.getEventGenre().trim());
        event.setDescription(request.getDescription());
        event.setBaseTicketPrice(request.getBaseTicketPrice());
        event.setAttendeeName(request.getAttendeeName().trim());
        event.setAttendeeRole(request.getAttendeeRole().trim());
        event.setVenue(request.getVenue());
        event.setEventDate(request.getEventDate());
        event.setSubmissionDate(LocalDateTime.now());
        event.setApprovalStatus(ApprovalStatus.PENDING);

        HostingEvent savedEvent = hostingEventRepository.save(event);

        // Create 1:1 Approval record
        Approval approval = new Approval();
        approval.setHostingEvent(savedEvent);
        approval.setOrganizer(organizer);
        approval.setApprovalStatus(ApprovalStatus.PENDING);
        approval.setApprovalDate(LocalDateTime.now());
        approval.setNotificationSent(false);

        Approval savedApproval = approvalRepository.save(approval);
        savedEvent.setApproval(savedApproval);

        // Trigger notification after transaction commit
        if (organizer.getOrganizerEmail() != null && !organizer.getOrganizerEmail().isBlank()) {
            eventPublisher.publishEvent(new EventSubmittedNotificationEvent(
                    organizer.getOrganizerEmail().trim(),
                    organizer.getOrganizerName(),
                    savedEvent.getEventName()
            ));
        }

        return mapToResponse(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByOrganizer(Long organizerId) {
        if (!organizerRepository.existsById(organizerId)) {
            throw new ResourceNotFoundException("Organizer not found with id: " + organizerId);
        }
        return hostingEventRepository.findByOrganizer_OrganizerIdOrderBySubmissionDateDesc(organizerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getHostedEvents() {
        return hostingEventRepository.findByApprovalStatusOrderBySubmissionDateDesc(ApprovalStatus.APPROVED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents(String statusFilter) {
        List<HostingEvent> events;
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !statusFilter.equalsIgnoreCase("ALL")) {
            ApprovalStatus status = ApprovalStatus.fromString(statusFilter.trim());
            events = hostingEventRepository.findByApprovalStatusOrderBySubmissionDateDesc(status);
        } else {
            events = hostingEventRepository.findAllByOrderBySubmissionDateDesc();
        }
        return events.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long eventId) {
        HostingEvent event = hostingEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        return mapToResponse(event);
    }

    private EventResponse mapToResponse(HostingEvent event) {
        EventResponse res = new EventResponse();
        res.setEventId(event.getEventId());
        if (event.getOrganizer() != null) {
            res.setOrganizerId(event.getOrganizer().getOrganizerId());
            res.setOrganizerName(event.getOrganizer().getOrganizerName());
            res.setOrganizerEmail(event.getOrganizer().getOrganizerEmail());
            res.setOrganizerContact(event.getOrganizer().getOrganizerContact());
        }
        res.setEventName(event.getEventName());
        res.setEventType(event.getEventType());
        res.setEventGenre(event.getEventGenre());
        res.setDescription(event.getDescription());
        res.setBaseTicketPrice(event.getBaseTicketPrice());
        res.setAttendeeName(event.getAttendeeName());
        res.setAttendeeRole(event.getAttendeeRole());
        res.setSubmissionDate(event.getSubmissionDate());
        res.setApprovalStatus(event.getApprovalStatus() != null ? event.getApprovalStatus().getDisplayValue() : "Pending");
        res.setRejectionReason(event.getRejectionReason());
        res.setVenue(event.getVenue());
        res.setEventDate(event.getEventDate());

        if (event.getApproval() != null) {
            res.setApprovalDate(event.getApproval().getApprovalDate());
            res.setNotificationSent(event.getApproval().getNotificationSent());
            if (res.getRejectionReason() == null) {
                res.setRejectionReason(event.getApproval().getRejectionReason());
            }
        }

        return res;
    }
}
