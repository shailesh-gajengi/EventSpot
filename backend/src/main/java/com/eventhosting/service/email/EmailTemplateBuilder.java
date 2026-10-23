package com.eventhosting.service.email;

import org.springframework.web.util.HtmlUtils;

public final class EmailTemplateBuilder {

    private EmailTemplateBuilder() {
    }

    public static String buildSubmissionEmailHtml(String artistName, String eventName) {
        String safeArtist = escape(artistName != null && !artistName.isBlank() ? artistName : "Artist");
        String safeEvent = escape(eventName);

        String content = """
            <h2 style="color: #0f172a; margin-top: 0; font-size: 20px; font-weight: 700;">Your Event Submission Has Been Received</h2>
            <p style="color: #334155; font-size: 15px; line-height: 1.6;">Hello <strong>%s</strong>,</p>
            <p style="color: #334155; font-size: 15px; line-height: 1.6;">
                Thank you for submitting your event to <strong>EventSpot</strong>. Your event proposal has been successfully registered in our system and is currently pending administrator moderation.
            </p>
            <div style="background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 16px 20px; margin: 24px 0;">
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr>
                        <td style="color: #64748b; font-size: 13px; font-weight: 600; text-transform: uppercase; padding-bottom: 6px;">Event Name</td>
                    </tr>
                    <tr>
                        <td style="color: #0f172a; font-size: 16px; font-weight: 700; padding-bottom: 12px;">%s</td>
                    </tr>
                    <tr>
                        <td style="color: #64748b; font-size: 13px; font-weight: 600; text-transform: uppercase; padding-bottom: 6px;">Current Status</td>
                    </tr>
                    <tr>
                        <td>
                            <span style="display: inline-block; background-color: #fef3c7; color: #92400e; border: 1px solid #fde68a; font-size: 12px; font-weight: 700; padding: 4px 10px; border-radius: 9999px; text-transform: uppercase; letter-spacing: 0.04em;">
                                Pending Approval
                            </span>
                        </td>
                    </tr>
                </table>
            </div>
            <p style="color: #334155; font-size: 14px; line-height: 1.6;">
                Our administration team examines every event proposal before it goes live. <strong>You will receive another email notification as soon as the admin approves or rejects your event.</strong>
            </p>
            <p style="color: #64748b; font-size: 13px; line-height: 1.5; margin-top: 20px;">
                You can also log into your Artist Dashboard at any time to check the live status of your event.
            </p>
            """.formatted(safeArtist, safeEvent);

        return wrapInBaseTemplate("Event Submission Received", content);
    }

    public static String buildSubmissionEmailText(String artistName, String eventName) {
        String name = artistName != null && !artistName.isBlank() ? artistName : "Artist";
        return """
            Hello %s,

            Thank you for submitting your event to EventSpot. Your event proposal has been successfully registered and is currently pending administrator moderation.

            Event Name: %s
            Current Status: Pending Approval

            You will receive another email notification as soon as the admin approves or rejects your event.
            You can also log into your Artist Dashboard at any time to check the live status of your event.

            ---
            EventSpot Platform Notifications
            """.formatted(name, eventName != null ? eventName : "");
    }

    public static String buildApprovalEmailHtml(String artistName, String eventName) {
        String safeArtist = escape(artistName != null && !artistName.isBlank() ? artistName : "Artist");
        String safeEvent = escape(eventName);

        String content = """
            <h2 style="color: #166534; margin-top: 0; font-size: 20px; font-weight: 700;">Congratulations! Your Event Has Been Approved</h2>
            <p style="color: #334155; font-size: 15px; line-height: 1.6;">Hello <strong>%s</strong>,</p>
            <p style="color: #334155; font-size: 15px; line-height: 1.6;">
                Great news! Our moderation team has reviewed and approved your event proposal. Your event is now officially <strong>Hosted</strong> on EventSpot and open for attendee ticket bookings.
            </p>
            <div style="background-color: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 8px; padding: 16px 20px; margin: 24px 0;">
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr>
                        <td style="color: #166534; font-size: 13px; font-weight: 600; text-transform: uppercase; padding-bottom: 6px;">Event Name</td>
                    </tr>
                    <tr>
                        <td style="color: #0f172a; font-size: 16px; font-weight: 700; padding-bottom: 12px;">%s</td>
                    </tr>
                    <tr>
                        <td style="color: #166534; font-size: 13px; font-weight: 600; text-transform: uppercase; padding-bottom: 6px;">Updated Status</td>
                    </tr>
                    <tr>
                        <td>
                            <span style="display: inline-block; background-color: #dcfce7; color: #166534; border: 1px solid #86efac; font-size: 12px; font-weight: 700; padding: 4px 10px; border-radius: 9999px; text-transform: uppercase; letter-spacing: 0.04em;">
                                Hosted / Approved
                            </span>
                        </td>
                    </tr>
                </table>
            </div>
            <p style="color: #334155; font-size: 14px; line-height: 1.6;">
                Your event is now actively listed on our public explore page where visitors can discover details and purchase tickets.
            </p>
            <p style="color: #64748b; font-size: 13px; line-height: 1.5; margin-top: 20px;">
                Log into your Artist Dashboard to view performance metrics, manage event listings, and monitor bookings.
            </p>
            """.formatted(safeArtist, safeEvent);

        return wrapInBaseTemplate("Event Approved & Hosted", content);
    }

    public static String buildApprovalEmailText(String artistName, String eventName) {
        String name = artistName != null && !artistName.isBlank() ? artistName : "Artist";
        return """
            Hello %s,

            Congratulations! Our moderation team has reviewed and approved your event proposal. Your event is now officially Hosted on EventSpot and open for attendee ticket bookings.

            Event Name: %s
            Updated Status: Hosted / Approved

            Your event is now actively listed on our public explore page.
            Log into your Artist Dashboard to view performance metrics, manage event listings, and monitor bookings.

            ---
            EventSpot Platform Notifications
            """.formatted(name, eventName != null ? eventName : "");
    }

    public static String buildRejectionEmailHtml(String artistName, String eventName, String rejectionReason) {
        String safeArtist = escape(artistName != null && !artistName.isBlank() ? artistName : "Artist");
        String safeEvent = escape(eventName);
        boolean hasReason = rejectionReason != null && !rejectionReason.isBlank();
        String safeReason = hasReason ? escape(rejectionReason.trim()) : null;

        String reasonBlock = hasReason ? """
            <div style="background-color: #fff1f2; border-left: 4px solid #f43f5e; border-radius: 4px; padding: 12px 16px; margin: 16px 0;">
                <p style="color: #9f1239; font-size: 12px; font-weight: 700; text-transform: uppercase; margin: 0 0 4px 0;">Admin Feedback / Reason:</p>
                <p style="color: #881337; font-size: 14px; margin: 0; line-height: 1.5;">%s</p>
            </div>
            """.formatted(safeReason) : "";

        String content = """
            <h2 style="color: #991b1b; margin-top: 0; font-size: 20px; font-weight: 700;">Update Regarding Your Event Submission</h2>
            <p style="color: #334155; font-size: 15px; line-height: 1.6;">Hello <strong>%s</strong>,</p>
            <p style="color: #334155; font-size: 15px; line-height: 1.6;">
                Thank you for your event proposal on EventSpot. After careful review, our administration team has decided not to approve the following event at this time:
            </p>
            <div style="background-color: #fef2f2; border: 1px solid #fecaca; border-radius: 8px; padding: 16px 20px; margin: 24px 0;">
                <table style="width: 100%%; border-collapse: collapse;">
                    <tr>
                        <td style="color: #991b1b; font-size: 13px; font-weight: 600; text-transform: uppercase; padding-bottom: 6px;">Event Name</td>
                    </tr>
                    <tr>
                        <td style="color: #0f172a; font-size: 16px; font-weight: 700; padding-bottom: 12px;">%s</td>
                    </tr>
                    <tr>
                        <td style="color: #991b1b; font-size: 13px; font-weight: 600; text-transform: uppercase; padding-bottom: 6px;">Status</td>
                    </tr>
                    <tr>
                        <td>
                            <span style="display: inline-block; background-color: #fee2e2; color: #991b1b; border: 1px solid #fca5a5; font-size: 12px; font-weight: 700; padding: 4px 10px; border-radius: 9999px; text-transform: uppercase; letter-spacing: 0.04em;">
                                Rejected
                            </span>
                        </td>
                    </tr>
                </table>
                %s
            </div>
            <p style="color: #334155; font-size: 14px; line-height: 1.6;">
                You can review this decision and feedback in your Artist Dashboard. You are welcome to revise your proposal or submit a new event that complies with our platform guidelines.
            </p>
            """.formatted(safeArtist, safeEvent, reasonBlock);

        return wrapInBaseTemplate("Event Submission Update", content);
    }

    public static String buildRejectionEmailText(String artistName, String eventName, String rejectionReason) {
        String name = artistName != null && !artistName.isBlank() ? artistName : "Artist";
        String reasonPart = rejectionReason != null && !rejectionReason.isBlank()
                ? "\nAdmin Feedback / Reason:\n" + rejectionReason.trim() + "\n"
                : "";
        return """
            Hello %s,

            Thank you for your event proposal on EventSpot. After careful review, our administration team has decided not to approve the following event at this time:

            Event Name: %s
            Status: Rejected
            %s
            You can review this decision and feedback in your Artist Dashboard. You are welcome to revise your proposal or submit a new event that complies with our platform guidelines.

            ---
            EventSpot Platform Notifications
            """.formatted(name, eventName != null ? eventName : "", reasonPart);
    }

    private static String wrapInBaseTemplate(String preheader, String bodyContent) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
            </head>
            <body style="margin: 0; padding: 0; background-color: #f1f5f9; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%%" style="background-color: #f1f5f9; padding: 32px 16px;">
                    <tr>
                        <td align="center">
                            <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%%" style="max-width: 580px; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -2px rgba(0, 0, 0, 0.05); border: 1px solid #e2e8f0;">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #4f46e5 0%%, #6366f1 100%%); padding: 24px 32px; text-align: left;">
                                        <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%%">
                                            <tr>
                                                <td>
                                                    <span style="color: #ffffff; font-size: 22px; font-weight: 800; letter-spacing: -0.02em;">EventSpot</span>
                                                    <span style="color: #e0e7ff; font-size: 12px; margin-left: 8px; font-weight: 500;">| Notifications</span>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <!-- Main Body -->
                                <tr>
                                    <td style="padding: 32px 32px 24px 32px;">
                                        %s
                                    </td>
                                </tr>
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color: #f8fafc; border-top: 1px solid #e2e8f0; padding: 20px 32px; text-align: center;">
                                        <p style="color: #94a3b8; font-size: 12px; margin: 0; line-height: 1.5;">
                                            This is an automated notification from the EventSpot platform.<br>
                                            Please do not reply directly to this email.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(escape(preheader), bodyContent);
    }

    private static String escape(String input) {
        if (input == null) return "";
        return HtmlUtils.htmlEscape(input);
    }
}
