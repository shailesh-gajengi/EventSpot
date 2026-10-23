package com.eventhosting.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class EventCreateRequest {

    @NotNull(message = "Organizer ID is required")
    private Long organizerId;

    @NotBlank(message = "Event name is required")
    @Size(max = 100, message = "Event name cannot exceed 100 characters")
    private String eventName;

    @NotBlank(message = "Event type is required")
    @Size(max = 50, message = "Event type cannot exceed 50 characters")
    private String eventType;

    @NotBlank(message = "Event genre is required")
    @Size(max = 50, message = "Event genre cannot exceed 50 characters")
    private String eventGenre;

    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Base ticket price cannot be negative")
    private BigDecimal baseTicketPrice;

    @NotBlank(message = "Attendee / Performer name is required")
    @Size(max = 100, message = "Attendee name cannot exceed 100 characters")
    private String attendeeName;

    @NotBlank(message = "Attendee role is required")
    @Size(max = 50, message = "Attendee role cannot exceed 50 characters")
    private String attendeeRole;

    private String venue;

    private String eventDate;

    public EventCreateRequest() {
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventGenre() {
        return eventGenre;
    }

    public void setEventGenre(String eventGenre) {
        this.eventGenre = eventGenre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBaseTicketPrice() {
        return baseTicketPrice;
    }

    public void setBaseTicketPrice(BigDecimal baseTicketPrice) {
        this.baseTicketPrice = baseTicketPrice;
    }

    public String getAttendeeName() {
        return attendeeName;
    }

    public void setAttendeeName(String attendeeName) {
        this.attendeeName = attendeeName;
    }

    public String getAttendeeRole() {
        return attendeeRole;
    }

    public void setAttendeeRole(String attendeeRole) {
        this.attendeeRole = attendeeRole;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
}
