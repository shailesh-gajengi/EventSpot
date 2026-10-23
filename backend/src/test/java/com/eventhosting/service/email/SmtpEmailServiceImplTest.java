package com.eventhosting.service.email;

import com.eventhosting.service.email.impl.SmtpEmailServiceImpl;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SmtpEmailServiceImplTest {

    private JavaMailSender mailSender;
    private SmtpEmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        mailSender = Mockito.mock(JavaMailSender.class);
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailService = new SmtpEmailServiceImpl(mailSender, "shaileshgajengi@gmail.com", "EventSpot");
    }

    @Test
    @DisplayName("Should gracefully skip sending when mailSender is null")
    void whenMailSenderIsNull_shouldNotThrow() {
        SmtpEmailServiceImpl serviceWithoutSender = new SmtpEmailServiceImpl(null, "shaileshgajengi@gmail.com", "EventSpot");
        assertDoesNotThrow(() -> serviceWithoutSender.sendEventSubmissionEmail("artist@example.com", "Alice", "Indie Rock Night"));
    }

    @Test
    @DisplayName("Should gracefully skip sending when recipient email is null or blank")
    void whenRecipientEmailIsBlank_shouldNotThrow() {
        assertDoesNotThrow(() -> emailService.sendEventSubmissionEmail(null, "Alice", "Indie Rock Night"));
        assertDoesNotThrow(() -> emailService.sendEventSubmissionEmail("   ", "Alice", "Indie Rock Night"));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should send email through JavaMailSender successfully")
    void whenValidEmail_shouldSendMimeMessage() {
        assertDoesNotThrow(() -> emailService.sendEventSubmissionEmail("caterpillarofmine123@gmail.com", "Alice", "Indie Rock Night"));
        assertDoesNotThrow(() -> emailService.sendEventApprovalEmail("caterpillarofmine123@gmail.com", "Alice", "Indie Rock Night"));
        assertDoesNotThrow(() -> emailService.sendEventRejectionEmail("caterpillarofmine123@gmail.com", "Alice", "Indie Rock Night", "Missing venue permit"));

        verify(mailSender, times(3)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("SMTP delivery error must not throw exception to caller")
    void whenMailSenderThrowsException_shouldCatchAndNotThrow() {
        doThrow(new MailSendException("SMTP connection timed out")).when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> emailService.sendEventApprovalEmail("artist@example.com", "Bob", "Standup Show"));
    }
}
