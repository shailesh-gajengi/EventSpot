package com.eventhosting.service.email;

import com.eventhosting.service.email.impl.ResendEmailServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ResendEmailServiceImplTest {

    @Test
    @DisplayName("Should gracefully skip sending and not throw when API key is blank")
    void whenApiKeyIsMissing_shouldNotAttemptHttpCallAndNotThrow() {
        RestClient.Builder builder = RestClient.builder();
        ResendEmailServiceImpl emailService = new ResendEmailServiceImpl(
                builder,
                "", // empty API key
                "onboarding@resend.dev",
                "https://api.resend.com"
        );

        assertDoesNotThrow(() ->
                emailService.sendEventSubmissionEmail("artist@example.com", "John", "Rock Concert"));
        assertDoesNotThrow(() ->
                emailService.sendEventApprovalEmail("artist@example.com", "John", "Rock Concert"));
        assertDoesNotThrow(() ->
                emailService.sendEventRejectionEmail("artist@example.com", "John", "Rock Concert", "Incomplete bio"));
    }

    @Test
    @DisplayName("Should gracefully skip sending when recipient email is null or blank")
    void whenRecipientEmailIsBlank_shouldNotThrow() {
        RestClient.Builder builder = RestClient.builder();
        ResendEmailServiceImpl emailService = new ResendEmailServiceImpl(
                builder,
                "re_test_key_12345",
                "onboarding@resend.dev",
                "https://api.resend.com"
        );

        assertDoesNotThrow(() ->
                emailService.sendEventSubmissionEmail(null, "John", "Rock Concert"));
        assertDoesNotThrow(() ->
                emailService.sendEventSubmissionEmail("   ", "John", "Rock Concert"));
    }

    @Test
    @DisplayName("Should successfully POST to Resend API when key is configured and verify Authorization header")
    void whenResendApiSucceeds_shouldSendSubmissionEmail() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder).build();

        mockServer.expect(requestTo("https://api.resend.com/emails"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer re_valid_key_123"))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess("{\"id\": \"msg_123456\"}", MediaType.APPLICATION_JSON));

        ResendEmailServiceImpl emailService = new ResendEmailServiceImpl(
                builder,
                "re_valid_key_123",
                "EventSpot <onboarding@resend.dev>",
                "https://api.resend.com"
        );

        assertDoesNotThrow(() ->
                emailService.sendEventSubmissionEmail("artist@example.com", "Alice", "Indie Fest"));

        mockServer.verify();
    }

    @Test
    @DisplayName("Email failure on Resend HTTP error (e.g. 403 / 422) must not throw exception to caller")
    void whenResendApiReturnsError_shouldCatchAndNotThrow() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder).build();

        mockServer.expect(requestTo("https://api.resend.com/emails"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .body("{\"statusCode\": 403, \"message\": \"You can only send to your own email address.\"}"));

        ResendEmailServiceImpl emailService = new ResendEmailServiceImpl(
                builder,
                "re_test_key_123",
                "EventSpot <onboarding@resend.dev>",
                "https://api.resend.com"
        );

        assertDoesNotThrow(() ->
                emailService.sendEventApprovalEmail("artist@example.com", "Bob", "Comedy Special"));

        mockServer.verify();
    }
}
