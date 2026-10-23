package com.eventhosting.service.email;

public interface EmailService {

    /**
     * Sends an email notification to the artist when their event is submitted.
     *
     * @param toEmail    Recipient email address (artist)
     * @param artistName Name of the artist/organizer
     * @param eventName  Name of the submitted event
     */
    void sendEventSubmissionEmail(String toEmail, String artistName, String eventName);

    /**
     * Sends an email notification to the artist when their event is approved by the admin.
     *
     * @param toEmail    Recipient email address (artist)
     * @param artistName Name of the artist/organizer
     * @param eventName  Name of the approved event
     */
    void sendEventApprovalEmail(String toEmail, String artistName, String eventName);

    /**
     * Sends an email notification to the artist when their event is rejected by the admin.
     *
     * @param toEmail         Recipient email address (artist)
     * @param artistName      Name of the artist/organizer
     * @param eventName       Name of the rejected event
     * @param rejectionReason Optional reason given by the admin
     */
    void sendEventRejectionEmail(String toEmail, String artistName, String eventName, String rejectionReason);
}
