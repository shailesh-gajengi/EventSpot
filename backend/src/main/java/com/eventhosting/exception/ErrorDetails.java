package com.eventhosting.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorDetails {

    private boolean success;
    private String message;
    private List<String> details;
    private LocalDateTime timestamp;

    public ErrorDetails() {
        this.success = false;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorDetails(String message, List<String> details) {
        this.success = false;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
