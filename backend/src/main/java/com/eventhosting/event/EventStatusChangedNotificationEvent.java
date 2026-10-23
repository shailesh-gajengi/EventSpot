package com.eventhosting.event;

import com.eventhosting.entity.ApprovalStatus;

public record EventStatusChangedNotificationEvent(
        String toEmail,
        String artistName,
        String eventName,
        ApprovalStatus oldStatus,
        ApprovalStatus newStatus,
        String rejectionReason
) {
}
