package com.eventhosting.service.email.impl;

import com.eventhosting.service.email.EmailService;
import com.eventhosting.service.email.EmailTemplateBuilder;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "mail.provider", havingValue = "smtp", matchIfMissing = true)
public class SmtpEmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(SmtpEmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String senderName;

    @Autowired
    public SmtpEmailServiceImpl(
            @Autowired(required = false) JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String fromEmail,
            @Value("${mail.from.name:EventSpot}") String senderName) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail != null ? fromEmail.trim() : "";
        this.senderName = senderName != null ? senderName.trim() : "EventSpot";
    }

    @Override
    public void sendEventSubmissionEmail(String toEmail, String artistName, String eventName) {
        String subject = "Your Event Submission Has Been Received";
        String html = EmailTemplateBuilder.buildSubmissionEmailHtml(artistName, eventName);
        String text = EmailTemplateBuilder.buildSubmissionEmailText(artistName, eventName);
        sendEmail(toEmail, subject, text, html);
    }

    @Override
    public void sendEventApprovalEmail(String toEmail, String artistName, String eventName) {
        String subject = "Congratulations! Your Event Has Been Approved";
        String html = EmailTemplateBuilder.buildApprovalEmailHtml(artistName, eventName);
        String text = EmailTemplateBuilder.buildApprovalEmailText(artistName, eventName);
        sendEmail(toEmail, subject, text, html);
    }

    @Override
    public void sendEventRejectionEmail(String toEmail, String artistName, String eventName, String rejectionReason) {
        String subject = "Update Regarding Your Event Submission";
        String html = EmailTemplateBuilder.buildRejectionEmailHtml(artistName, eventName, rejectionReason);
        String text = EmailTemplateBuilder.buildRejectionEmailText(artistName, eventName, rejectionReason);
        sendEmail(toEmail, subject, text, html);
    }

    private void sendEmail(String toEmail, String subject, String textContent, String htmlContent) {
        if (mailSender == null) {
            logger.warn("JavaMailSender is not initialized. Ensure SMTP properties (spring.mail.host, username, password) are set.");
            return;
        }

        if (toEmail == null || toEmail.isBlank()) {
            logger.warn("Cannot send email: recipient address is null or empty for subject '{}'.", subject);
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            if (!fromEmail.isEmpty()) {
                helper.setFrom(new InternetAddress(fromEmail, senderName));
                helper.setReplyTo(new InternetAddress(fromEmail, senderName));
            }
            helper.setTo(toEmail.trim());
            helper.setSubject(subject);
            helper.setText(textContent, htmlContent); // multipart/alternative with plain text fallback

            // Deliverability headers
            mimeMessage.setHeader("Auto-Submitted", "auto-generated");
            mimeMessage.setHeader("X-Auto-Response-Suppress", "All");
            mimeMessage.setHeader("X-Mailer", "EventSpot Notification System");

            mailSender.send(mimeMessage);
            logger.info("Email notification successfully sent via Gmail SMTP to [{}] with subject '{}'.", toEmail, subject);
        } catch (Exception ex) {
            // Guarantee email failure does not affect database transactions or caller
            logger.error("Failed to deliver email via SMTP to [{}] with subject '{}' due to error: {}",
                    toEmail, subject, ex.getMessage());
        }
    }
}
