package com.eventhosting.service.impl;

import com.eventhosting.dto.request.ApprovalActionRequest;
import com.eventhosting.dto.response.ApprovalResponse;
import com.eventhosting.entity.Approval;
import com.eventhosting.entity.ApprovalStatus;
import com.eventhosting.entity.HostingEvent;
import com.eventhosting.entity.Organizer;
import com.eventhosting.event.EventStatusChangedNotificationEvent;
import com.eventhosting.repository.ApprovalRepository;
import com.eventhosting.repository.HostingEventRepository;
import com.eventhosting.repository.OrganizerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalServiceImplTest {

    @Mock
    private ApprovalRepository approvalRepository;

    @Mock
    private HostingEventRepository hostingEventRepository;

    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ApprovalServiceImpl approvalService;

    @BeforeEach
    void setUp() {
        approvalService = new ApprovalServiceImpl(
                approvalRepository,
                hostingEventRepository,
                organizerRepository,
                eventPublisher
        );
    }

    @Test
    @DisplayName("Approving a pending event should update status and publish approval notification event")
    void whenApprovingPendingEvent_shouldPublishApprovalNotification() {
        Long eventId = 202L;
        Organizer organizer = new Organizer("Adele", "555-4321", "adele@music.com", "pass123", true);
        organizer.setOrganizerId(5L);

        HostingEvent event = new HostingEvent();
        event.setEventId(eventId);
        event.setEventName("Live at the Royal Hall");
        event.setApprovalStatus(ApprovalStatus.PENDING);
        event.setOrganizer(organizer);

        Approval approval = new Approval(event, organizer);
        approval.setApprovalId(88L);

        when(hostingEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(approvalRepository.findByHostingEvent_EventId(eventId)).thenReturn(Optional.of(approval));
        when(hostingEventRepository.save(any(HostingEvent.class))).thenAnswer(i -> i.getArgument(0));
        when(approvalRepository.save(any(Approval.class))).thenAnswer(i -> i.getArgument(0));

        ApprovalActionRequest request = new ApprovalActionRequest("Approved", null);

        ApprovalResponse response = approvalService.processApproval(eventId, request);

        assertNotNull(response);
        assertEquals("Approved", response.getApprovalStatus());

        ArgumentCaptor<EventStatusChangedNotificationEvent> captor =
                ArgumentCaptor.forClass(EventStatusChangedNotificationEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());

        EventStatusChangedNotificationEvent published = captor.getValue();
        assertEquals("adele@music.com", published.toEmail());
        assertEquals("Adele", published.artistName());
        assertEquals("Live at the Royal Hall", published.eventName());
        assertEquals(ApprovalStatus.PENDING, published.oldStatus());
        assertEquals(ApprovalStatus.APPROVED, published.newStatus());
        assertNull(published.rejectionReason());
    }

    @Test
    @DisplayName("Rejecting a pending event should update status and publish rejection notification with reason")
    void whenRejectingPendingEvent_shouldPublishRejectionNotificationWithReason() {
        Long eventId = 203L;
        Organizer organizer = new Organizer("Adele", "555-4321", "adele@music.com", "pass123", true);
        organizer.setOrganizerId(5L);

        HostingEvent event = new HostingEvent();
        event.setEventId(eventId);
        event.setEventName("Live at the Royal Hall");
        event.setApprovalStatus(ApprovalStatus.PENDING);
        event.setOrganizer(organizer);

        Approval approval = new Approval(event, organizer);
        approval.setApprovalId(89L);

        when(hostingEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(approvalRepository.findByHostingEvent_EventId(eventId)).thenReturn(Optional.of(approval));
        when(hostingEventRepository.save(any(HostingEvent.class))).thenAnswer(i -> i.getArgument(0));
        when(approvalRepository.save(any(Approval.class))).thenAnswer(i -> i.getArgument(0));

        ApprovalActionRequest request = new ApprovalActionRequest("Rejected", "Incomplete insurance documents.");

        ApprovalResponse response = approvalService.processApproval(eventId, request);

        assertNotNull(response);
        assertEquals("Rejected", response.getApprovalStatus());
        assertEquals("Incomplete insurance documents.", response.getRejectionReason());

        ArgumentCaptor<EventStatusChangedNotificationEvent> captor =
                ArgumentCaptor.forClass(EventStatusChangedNotificationEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());

        EventStatusChangedNotificationEvent published = captor.getValue();
        assertEquals("adele@music.com", published.toEmail());
        assertEquals("Adele", published.artistName());
        assertEquals("Live at the Royal Hall", published.eventName());
        assertEquals(ApprovalStatus.PENDING, published.oldStatus());
        assertEquals(ApprovalStatus.REJECTED, published.newStatus());
        assertEquals("Incomplete insurance documents.", published.rejectionReason());
    }

    @Test
    @DisplayName("Submitting same status (already APPROVED -> Approved) should NOT publish duplicate notification")
    void whenStatusUnchanged_shouldNotPublishDuplicateNotification() {
        Long eventId = 204L;
        Organizer organizer = new Organizer("Adele", "555-4321", "adele@music.com", "pass123", true);

        HostingEvent event = new HostingEvent();
        event.setEventId(eventId);
        event.setEventName("Live at the Royal Hall");
        event.setApprovalStatus(ApprovalStatus.APPROVED); // Already approved
        event.setOrganizer(organizer);

        Approval approval = new Approval(event, organizer);
        approval.setApprovalStatus(ApprovalStatus.APPROVED);

        when(hostingEventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(approvalRepository.findByHostingEvent_EventId(eventId)).thenReturn(Optional.of(approval));
        when(hostingEventRepository.save(any(HostingEvent.class))).thenAnswer(i -> i.getArgument(0));
        when(approvalRepository.save(any(Approval.class))).thenAnswer(i -> i.getArgument(0));

        ApprovalActionRequest request = new ApprovalActionRequest("Approved", null);

        ApprovalResponse response = approvalService.processApproval(eventId, request);

        assertNotNull(response);
        assertEquals("Approved", response.getApprovalStatus());

        // Verify that eventPublisher was NOT called because status did not change
        verifyNoInteractions(eventPublisher);
    }
}
