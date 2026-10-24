package com.eventhosting.service.email.impl;

import com.eventhosting.service.email.EmailService;
import com.eventhosting.service.email.EmailTemplateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "mail.provider", havingValue = "brevo")
public class BrevoEmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(BrevoEmailServiceImpl.class);

    private final RestClient restClient;
    private final String apiKey;
    private final String senderName;
    private final String senderEmail;

    @Autowired
    public BrevoEmailServiceImpl(
            RestClient.Builder restClientBuilder,
            @Value("${brevo.api-key:${BREVO_API_KEY:}}") String apiKey,
            @Value("${brevo.sender-name:${BREVO_SENDER_NAME:EventSpot}}") String senderName,
            @Value("${brevo.sender-email:${BREVO_SENDER_EMAIL:shaileshgajengi@gmail.com}}") String senderEmail,
            @Value("${brevo.base-url:https://api.brevo.com/v3}") String baseUrl) {

        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.senderName = senderName != null && !senderName.isBlank() ? senderName.trim() : "EventSpot";
        this.senderEmail = senderEmail != null && !senderEmail.isBlank() ? senderEmail.trim() : "shaileshgajengi@gmail.com";

        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public void sendEventSubmissionEmail(String toEmail, String artistName, String eventName) {
        String subject = "Your Event Submission Has Been Received";
        String html = EmailTemplateBuilder.buildSubmissionEmailHtml(artistName, eventName);
        String text = EmailTemplateBuilder.buildSubmissionEmailText(artistName, eventName);
        sendEmail(toEmail, artistName, subject, text, html);
    }

    @Override
    public void sendEventApprovalEmail(String toEmail, String artistName, String eventName) {
        String subject = "Congratulations! Your Event Has Been Approved";
        String html = EmailTemplateBuilder.buildApprovalEmailHtml(artistName, eventName);
        String text = EmailTemplateBuilder.buildApprovalEmailText(artistName, eventName);
        sendEmail(toEmail, artistName, subject, text, html);
    }

    @Override
    public void sendEventRejectionEmail(String toEmail, String artistName, String eventName, String rejectionReason) {
        String subject = "Update Regarding Your Event Submission";
        String html = EmailTemplateBuilder.buildRejectionEmailHtml(artistName, eventName, rejectionReason);
        String text = EmailTemplateBuilder.buildRejectionEmailText(artistName, eventName, rejectionReason);
        sendEmail(toEmail, artistName, subject, text, html);
    }

    private void sendEmail(String toEmail, String recipientName, String subject, String textContent, String htmlContent) {
        if (apiKey.isEmpty()) {
            logger.warn("Brevo API key is not configured (BREVO_API_KEY). Email notification to [{}] with subject '{}' was skipped.",
                    toEmail, subject);
            return;
        }

        if (toEmail == null || toEmail.isBlank()) {
            logger.warn("Cannot send email: recipient address is null or empty for subject '{}'.", subject);
            return;
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", Map.of("name", senderName, "email", senderEmail));
            
            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", toEmail.trim());
            if (recipientName != null && !recipientName.isBlank()) {
                recipient.put("name", recipientName.trim());
            }
            payload.put("to", List.of(recipient));

            payload.put("subject", subject);
            payload.put("htmlContent", htmlContent);
            if (textContent != null && !textContent.isBlank()) {
                payload.put("textContent", textContent);
            }

            ResponseEntity<String> response = restClient.post()
                    .uri("/smtp/email")
                    .header("api-key", apiKey)
                    .body(payload)
                    .retrieve()
                    .toEntity(String.class);

            logger.info("Email notification successfully sent via Brevo HTTPS API to [{}] with subject '{}'. Response status: {}",
                    toEmail, subject, response.getStatusCode());
        } catch (RestClientResponseException ex) {
            // Log delivery failure without exposing the API key
            logger.error("Brevo API rejected email delivery to [{}] with subject '{}'. HTTP status: {}. Response: {}",
                    toEmail, subject, ex.getStatusCode(), ex.getResponseBodyAsString());
        } catch (Exception ex) {
            // Log general/network failure without exposing the API key
            logger.error("Failed to deliver email via Brevo to [{}] with subject '{}' due to error: {}",
                    toEmail, subject, ex.getMessage());
        }
    }
}
