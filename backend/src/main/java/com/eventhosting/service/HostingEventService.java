package com.eventhosting.service;

import com.eventhosting.dto.request.EventCreateRequest;
import com.eventhosting.dto.response.EventResponse;

import java.util.List;

public interface HostingEventService {

    EventResponse submitEvent(EventCreateRequest request);

    List<EventResponse> getEventsByOrganizer(Long organizerId);

    List<EventResponse> getHostedEvents();

    List<EventResponse> getAllEvents(String statusFilter);

    EventResponse getEventById(Long eventId);
}
