package com.eventhosting.listener;

import com.eventhosting.entity.ApprovalStatus;
import com.eventhosting.event.EventStatusChangedNotificationEvent;
import com.eventhosting.event.EventSubmittedNotificationEvent;
import com.eventhosting.service.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EventNotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(EventNotificationListener.class);

    private final EmailService emailService;

    public EventNotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Triggered strictly after the event creation transaction has successfully committed.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onEventSubmitted(EventSubmittedNotificationEvent event) {
        try {
            logger.info("Transaction committed for event submission '{}'. Sending confirmation email to [{}]...",
                    event.eventName(), event.toEmail());
            emailService.sendEventSubmissionEmail(event.toEmail(), event.artistName(), event.eventName());
        } catch (Exception ex) {
            // Guard against unexpected failures so they never affect the caller
            logger.error("Error in onEventSubmitted notification listener for [{}]: {}", event.toEmail(), ex.getMessage());
        }
    }

    /**
     * Triggered strictly after the event approval/rejection status change has successfully committed.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onEventStatusChanged(EventStatusChangedNotificationEvent event) {
        try {
            // Prevent duplicate notifications if the status hasn't actually changed
            if (event.oldStatus() == event.newStatus()) {
                logger.info("Notification skipped for event '{}': status remains '{}' (no change).",
                        event.eventName(), event.newStatus());
                return;
            }

            logger.info("Transaction committed for status update on event '{}' ({} -> {}). Sending notification to [{}]...",
                    event.eventName(), event.oldStatus(), event.newStatus(), event.toEmail());

            if (event.newStatus() == ApprovalStatus.APPROVED) {
                emailService.sendEventApprovalEmail(event.toEmail(), event.artistName(), event.eventName());
            } else if (event.newStatus() == ApprovalStatus.REJECTED) {
                emailService.sendEventRejectionEmail(event.toEmail(), event.artistName(), event.eventName(), event.rejectionReason());
            }
        } catch (Exception ex) {
            logger.error("Error in onEventStatusChanged notification listener for [{}]: {}", event.toEmail(), ex.getMessage());
        }
    }
}
