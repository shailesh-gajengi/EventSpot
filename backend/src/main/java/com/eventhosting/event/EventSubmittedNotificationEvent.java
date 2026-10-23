package com.eventhosting.event;

public record EventSubmittedNotificationEvent(
        String toEmail,
        String artistName,
        String eventName
) {
}
