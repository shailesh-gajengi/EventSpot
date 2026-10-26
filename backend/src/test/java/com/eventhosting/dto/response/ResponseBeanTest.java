package com.eventhosting.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseBeanTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should create success response with default message")
    void testSuccessWithBodyOnly() {
        String testData = "sample-payload";
        ResponseBean<String> response = ResponseBean.success(testData);

        assertEquals(0, response.getErrorCode());
        assertEquals("Success", response.getErrorMsg());
        assertEquals(testData, response.getBody());
    }

    @Test
    @DisplayName("Should create success response with custom message")
    void testSuccessWithCustomMessage() {
        String testData = "user-profile";
        ResponseBean<String> response = ResponseBean.success("Retrieved successfully", testData);

        assertEquals(0, response.getErrorCode());
        assertEquals("Retrieved successfully", response.getErrorMsg());
        assertEquals(testData, response.getBody());
    }

    @Test
    @DisplayName("Should create error response with error code and error message")
    void testErrorWithCodeAndMessage() {
        ResponseBean<Void> response = ResponseBean.error(404, "User not found");

        assertEquals(404, response.getErrorCode());
        assertEquals("User not found", response.getErrorMsg());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Should create error response with error code, message and body")
    void testErrorWithCodeMessageAndBody() {
        ResponseBean<String> response = ResponseBean.error(400, "Validation failed", "Invalid email format");

        assertEquals(400, response.getErrorCode());
        assertEquals("Validation failed", response.getErrorMsg());
        assertEquals("Invalid email format", response.getBody());
    }

    @Test
    @DisplayName("Should serialize into JSON containing exactly errorCode, errorMsg, and body")
    void testJsonSerialization() throws Exception {
        ResponseBean<String> response = ResponseBean.success("Event created successfully", "Event#101");
        String json = objectMapper.writeValueAsString(response);

        assertTrue(json.contains("\"errorCode\":0"));
        assertTrue(json.contains("\"errorMsg\":\"Event created successfully\""));
        assertTrue(json.contains("\"body\":\"Event#101\""));
    }
}
