package com.lonework.corners.event.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.event.model.DTO.EventCreateRequest;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.event.model.EventSearchParameters;
import com.lonework.corners.event.service.EventService;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;


@Service
public class EventFacade {

    @Inject
    EventService eventService;

    public Event createEvent(EventCreateRequest eventCreateRequest, String createdBy) {
        return eventService.createEvent(eventCreateRequest, createdBy);
    }

    public Event updateEvent(EventCreateRequest eventCreateRequest, Long id) {
        return eventService.updatedEvent(eventCreateRequest, id);
    }

    public void deleteEvent(Long id) {
        eventService.deleteEvent(id);
    }

    public Event getEvent(Long id) {
        return eventService.getEvent(id);
    }

    public PagedResult<Event> findEventByParameters(EventSearchParameters eventSearchParameters, PagingQueryParams queryParams) {
        return eventService.findEventByParameters(eventSearchParameters, queryParams);
    }
}
