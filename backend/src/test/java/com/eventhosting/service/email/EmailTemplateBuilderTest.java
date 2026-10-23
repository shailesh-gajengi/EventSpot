package com.eventhosting.service.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailTemplateBuilderTest {

    @Test
    @DisplayName("Submission email template should contain artist name, event name, pending status, and follow-up explanation")
    void testBuildSubmissionEmailHtml() {
        String artist = "Luna Eclipse";
        String event = "Midnight Jazz Fest";

        String html = EmailTemplateBuilder.buildSubmissionEmailHtml(artist, event);

        assertTrue(html.contains("Your Event Submission Has Been Received"));
        assertTrue(html.contains("Luna Eclipse"));
        assertTrue(html.contains("Midnight Jazz Fest"));
        assertTrue(html.contains("Pending Approval"));
        assertTrue(html.contains("You will receive another email notification as soon as the admin approves or rejects your event"));
        assertTrue(html.contains("EventSpot"));
    }

    @Test
    @DisplayName("Approval email template should contain congratulations, event name, and hosted status")
    void testBuildApprovalEmailHtml() {
        String artist = "The Retro Waves";
        String event = "Neon Synth Showcase";

        String html = EmailTemplateBuilder.buildApprovalEmailHtml(artist, event);

        assertTrue(html.contains("Congratulations! Your Event Has Been Approved"));
        assertTrue(html.contains("The Retro Waves"));
        assertTrue(html.contains("Neon Synth Showcase"));
        assertTrue(html.contains("Hosted / Approved"));
        assertTrue(html.contains("open for attendee ticket bookings"));
    }

    @Test
    @DisplayName("Rejection email template should include event name, rejection status, and reason when present")
    void testBuildRejectionEmailHtmlWithReason() {
        String artist = "DJ Pulse";
        String event = "Rooftop Sunset Session";
        String reason = "Missing venue permit and safety clearance.";

        String html = EmailTemplateBuilder.buildRejectionEmailHtml(artist, event, reason);

        assertTrue(html.contains("Update Regarding Your Event Submission"));
        assertTrue(html.contains("DJ Pulse"));
        assertTrue(html.contains("Rooftop Sunset Session"));
        assertTrue(html.contains("Rejected"));
        assertTrue(html.contains("Missing venue permit and safety clearance."));
    }

    @Test
    @DisplayName("Rejection email template should handle null or empty rejection reason gracefully")
    void testBuildRejectionEmailHtmlWithoutReason() {
        String artist = "DJ Pulse";
        String event = "Rooftop Sunset Session";

        String html = EmailTemplateBuilder.buildRejectionEmailHtml(artist, event, null);

        assertTrue(html.contains("Update Regarding Your Event Submission"));
        assertTrue(html.contains("Rooftop Sunset Session"));
        assertTrue(html.contains("Rejected"));
        assertFalse(html.contains("Admin Feedback / Reason:"));
    }

    @Test
    @DisplayName("Email templates should HTML-escape inputs to prevent injection")
    void testHtmlEscaping() {
        String dangerousArtist = "Artist <script>alert('xss')</script>";
        String dangerousEvent = "Rock & Roll <Live>";

        String html = EmailTemplateBuilder.buildSubmissionEmailHtml(dangerousArtist, dangerousEvent);

        assertFalse(html.contains("<script>"));
        assertTrue(html.contains("&lt;script&gt;"));
        assertTrue(html.contains("Rock &amp; Roll &lt;Live&gt;"));
    }
}
