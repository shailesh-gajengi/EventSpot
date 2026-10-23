package com.eventhosting.listener;

import com.eventhosting.entity.ApprovalStatus;
import com.eventhosting.event.EventStatusChangedNotificationEvent;
import com.eventhosting.event.EventSubmittedNotificationEvent;
import com.eventhosting.service.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventNotificationListenerTest {

    @Mock
    private EmailService emailService;

    private EventNotificationListener listener;

    @BeforeEach
    void setUp() {
        listener = new EventNotificationListener(emailService);
    }

    @Test
    @DisplayName("Should invoke emailService.sendEventSubmissionEmail upon event submission")
    void testOnEventSubmitted() {
        EventSubmittedNotificationEvent event = new EventSubmittedNotificationEvent(
                "artist@band.com",
                "Alex Turner",
                "Arctic Sounds Live"
        );

        listener.onEventSubmitted(event);

        verify(emailService, times(1))
                .sendEventSubmissionEmail("artist@band.com", "Alex Turner", "Arctic Sounds Live");
    }

    @Test
    @DisplayName("Should invoke emailService.sendEventApprovalEmail when status changes to APPROVED")
    void testOnEventApproved() {
        EventStatusChangedNotificationEvent event = new EventStatusChangedNotificationEvent(
                "artist@band.com",
                "Alex Turner",
                "Arctic Sounds Live",
                ApprovalStatus.PENDING,
                ApprovalStatus.APPROVED,
                null
        );

        listener.onEventStatusChanged(event);

        verify(emailService, times(1))
                .sendEventApprovalEmail("artist@band.com", "Alex Turner", "Arctic Sounds Live");
        verify(emailService, never()).sendEventRejectionEmail(anyString(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should invoke emailService.sendEventRejectionEmail when status changes to REJECTED")
    void testOnEventRejected() {
        EventStatusChangedNotificationEvent event = new EventStatusChangedNotificationEvent(
                "artist@band.com",
                "Alex Turner",
                "Arctic Sounds Live",
                ApprovalStatus.PENDING,
                ApprovalStatus.REJECTED,
                "Venue clearance missing"
        );

        listener.onEventStatusChanged(event);

        verify(emailService, times(1))
                .sendEventRejectionEmail("artist@band.com", "Alex Turner", "Arctic Sounds Live", "Venue clearance missing");
        verify(emailService, never()).sendEventApprovalEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should prevent duplicate notification when oldStatus equals newStatus")
    void testPreventDuplicateNotificationWhenStatusUnchanged() {
        // e.g., Already APPROVED, admin approves again
        EventStatusChangedNotificationEvent event = new EventStatusChangedNotificationEvent(
                "artist@band.com",
                "Alex Turner",
                "Arctic Sounds Live",
                ApprovalStatus.APPROVED,
                ApprovalStatus.APPROVED,
                null
        );

        listener.onEventStatusChanged(event);

        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("Listener must not throw even if emailService throws an unexpected error")
    void testErrorInEmailServiceDoesNotPropagate() {
        doThrow(new RuntimeException("Resend connection failed"))
                .when(emailService)
                .sendEventSubmissionEmail(anyString(), anyString(), anyString());

        EventSubmittedNotificationEvent event = new EventSubmittedNotificationEvent(
                "artist@band.com",
                "Alex Turner",
                "Arctic Sounds Live"
        );

        assertDoesNotThrow(() -> listener.onEventSubmitted(event));
    }
}
