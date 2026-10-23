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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "mail.provider", havingValue = "resend")
public class ResendEmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(ResendEmailServiceImpl.class);

    private final RestClient restClient;
    private final String apiKey;
    private final String fromEmail;
    private final String replyToEmail;

    @Autowired
    public ResendEmailServiceImpl(
            RestClient.Builder restClientBuilder,
            @Value("${resend.api-key:}") String apiKey,
            @Value("${resend.from-email:EventSpot <onboarding@resend.dev>}") String fromEmail,
            @Value("${resend.reply-to:${RESEND_REPLY_TO:shaileshgajengi@gmail.com}}") String replyToEmail,
            @Value("${resend.base-url:https://api.resend.com}") String baseUrl) {

        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.fromEmail = fromEmail != null && !fromEmail.isBlank() ? fromEmail.trim() : "EventSpot <onboarding@resend.dev>";
        this.replyToEmail = replyToEmail != null && !replyToEmail.isBlank() ? replyToEmail.trim() : null;

        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public ResendEmailServiceImpl(
            RestClient.Builder restClientBuilder,
            String apiKey,
            String fromEmail,
            String baseUrl) {
        this(restClientBuilder, apiKey, fromEmail, null, baseUrl);
    }

    @Override
    public void sendEventSubmissionEmail(String toEmail, String artistName, String eventName) {
        String subject = "Your Event Submission Has Been Received";
        String html = EmailTemplateBuilder.buildSubmissionEmailHtml(artistName, eventName);
        sendEmail(toEmail, subject, html);
    }

    @Override
    public void sendEventApprovalEmail(String toEmail, String artistName, String eventName) {
        String subject = "Congratulations! Your Event Has Been Approved";
        String html = EmailTemplateBuilder.buildApprovalEmailHtml(artistName, eventName);
        sendEmail(toEmail, subject, html);
    }

    @Override
    public void sendEventRejectionEmail(String toEmail, String artistName, String eventName, String rejectionReason) {
        String subject = "Update Regarding Your Event Submission";
        String html = EmailTemplateBuilder.buildRejectionEmailHtml(artistName, eventName, rejectionReason);
        sendEmail(toEmail, subject, html);
    }

    private void sendEmail(String toEmail, String subject, String htmlContent) {
        if (apiKey.isEmpty()) {
            logger.warn("Resend API key is not configured (RESEND_API_KEY). Email notification to [{}] with subject '{}' was skipped.",
                    toEmail, subject);
            return;
        }

        if (toEmail == null || toEmail.isBlank()) {
            logger.warn("Cannot send email: recipient address is null or empty for subject '{}'.", subject);
            return;
        }

        try {
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("from", fromEmail);
            payload.put("to", List.of(toEmail.trim()));
            payload.put("subject", subject);
            payload.put("html", htmlContent);
            if (replyToEmail != null && !replyToEmail.isBlank()) {
                payload.put("reply_to", replyToEmail);
            }

            ResponseEntity<String> response = restClient.post()
                    .uri("/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .body(payload)
                    .retrieve()
                    .toEntity(String.class);

            logger.info("Email notification successfully sent via Resend to [{}] with subject '{}'. Response status: {}",
                    toEmail, subject, response.getStatusCode());
        } catch (RestClientResponseException ex) {
            // Log delivery failure without exposing the API key
            logger.error("Resend API rejected email delivery to [{}] with subject '{}'. HTTP status: {}. Response: {}",
                    toEmail, subject, ex.getStatusCode(), ex.getResponseBodyAsString());
        } catch (Exception ex) {
            // Log general/network failure without exposing the API key
            logger.error("Failed to deliver email via Resend to [{}] with subject '{}' due to error: {}",
                    toEmail, subject, ex.getMessage());
        }
    }
}
