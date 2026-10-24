package com.eventhosting.service.email;

import com.eventhosting.service.email.impl.BrevoEmailServiceImpl;
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

class BrevoEmailServiceImplTest {

    @Test
    @DisplayName("Should gracefully skip sending when Brevo API key is missing")
    void whenApiKeyMissing_shouldNotThrow() {
        RestClient.Builder builder = RestClient.builder();
        BrevoEmailServiceImpl service = new BrevoEmailServiceImpl(
                builder,
                "",
                "EventSpot",
                "shaileshgajengi@gmail.com",
                "https://api.brevo.com/v3"
        );

        assertDoesNotThrow(() -> service.sendEventSubmissionEmail("artist@example.com", "John", "Rock Concert"));
        assertDoesNotThrow(() -> service.sendEventApprovalEmail("artist@example.com", "John", "Rock Concert"));
        assertDoesNotThrow(() -> service.sendEventRejectionEmail("artist@example.com", "John", "Rock Concert", "Incomplete bio"));
    }

    @Test
    @DisplayName("Should successfully POST to Brevo API over HTTPS with api-key header")
    void whenBrevoApiSucceeds_shouldSendSubmissionEmail() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder).build();

        mockServer.expect(requestTo("https://api.brevo.com/v3/smtp/email"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("api-key", "xkeysib-valid-key-123"))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess("{\"messageId\": \"<abc12345@smtp-relay.brevo.com>\"}", MediaType.APPLICATION_JSON));

        BrevoEmailServiceImpl service = new BrevoEmailServiceImpl(
                builder,
                "xkeysib-valid-key-123",
                "EventSpot",
                "shaileshgajengi@gmail.com",
                "https://api.brevo.com/v3"
        );

        assertDoesNotThrow(() -> service.sendEventApprovalEmail("caterpillarofmine123@gmail.com", "Alice", "Indie Fest"));
        mockServer.verify();
    }

    @Test
    @DisplayName("Brevo HTTP error (e.g. 400 or 401) must not throw exception to caller")
    void whenBrevoApiReturnsError_shouldCatchAndNotThrow() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(builder).build();

        mockServer.expect(requestTo("https://api.brevo.com/v3/smtp/email"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Key not found\"}"));

        BrevoEmailServiceImpl service = new BrevoEmailServiceImpl(
                builder,
                "xkeysib-bad-key",
                "EventSpot",
                "shaileshgajengi@gmail.com",
                "https://api.brevo.com/v3"
        );

        assertDoesNotThrow(() -> service.sendEventSubmissionEmail("caterpillarofmine123@gmail.com", "Bob", "Comedy Show"));
        mockServer.verify();
    }
}
