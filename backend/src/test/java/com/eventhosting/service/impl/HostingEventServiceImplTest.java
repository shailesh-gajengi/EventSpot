package com.eventhosting.service.impl;

import com.eventhosting.dto.request.EventCreateRequest;
import com.eventhosting.dto.response.EventResponse;
import com.eventhosting.entity.Approval;
import com.eventhosting.entity.ApprovalStatus;
import com.eventhosting.entity.HostingEvent;
import com.eventhosting.entity.Organizer;
import com.eventhosting.event.EventSubmittedNotificationEvent;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HostingEventServiceImplTest {

    @Mock
    private HostingEventRepository hostingEventRepository;

    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private ApprovalRepository approvalRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private HostingEventServiceImpl hostingEventService;

    @BeforeEach
    void setUp() {
        hostingEventService = new HostingEventServiceImpl(
                hostingEventRepository,
                organizerRepository,
                approvalRepository,
                eventPublisher
        );
    }

    @Test
    @DisplayName("Submitting an event should save the event and publish EventSubmittedNotificationEvent")
    void testSubmitEventPublishesNotificationEvent() {
        Long organizerId = 10L;
        Organizer organizer = new Organizer("Dave Grohl", "555-1234", "dave@foofighters.com", "pass123", true);
        organizer.setOrganizerId(organizerId);

        when(organizerRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(hostingEventRepository.save(any(HostingEvent.class))).thenAnswer(invocation -> {
            HostingEvent e = invocation.getArgument(0);
            e.setEventId(101L);
            return e;
        });
        when(approvalRepository.save(any(Approval.class))).thenAnswer(invocation -> {
            Approval a = invocation.getArgument(0);
            a.setApprovalId(501L);
            return a;
        });

        EventCreateRequest request = new EventCreateRequest();
        request.setOrganizerId(organizerId);
        request.setEventName("Rock Revolution");
        request.setEventType("Concert");
        request.setEventGenre("Rock");
        request.setAttendeeName("Dave Grohl");
        request.setAttendeeRole("Frontman");
        request.setBaseTicketPrice(BigDecimal.valueOf(50.00));

        EventResponse response = hostingEventService.submitEvent(request);

        assertNotNull(response);
        assertEquals(101L, response.getEventId());
        assertEquals("Rock Revolution", response.getEventName());
        assertEquals("Pending", response.getApprovalStatus());

        // Verify that EventSubmittedNotificationEvent was published with correct artist and email
        ArgumentCaptor<EventSubmittedNotificationEvent> eventCaptor =
                ArgumentCaptor.forClass(EventSubmittedNotificationEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        EventSubmittedNotificationEvent captured = eventCaptor.getValue();
        assertEquals("dave@foofighters.com", captured.toEmail());
        assertEquals("Dave Grohl", captured.artistName());
        assertEquals("Rock Revolution", captured.eventName());
    }
}
