package com.lonework.corners.event.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.event.model.EventSearchParameters;
import com.lonework.corners.event.service.EventService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/public/events")
public class PublicEventController {

    @Autowired
    EventService eventService;

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/{id}")
    @PermitAll
    public Event getEvent(@PathVariable("id") Long id) {
        return eventService.getEvent(id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("")
    @PermitAll
    public PagedResult<Event> findEvent(@ModelAttribute EventSearchParameters eventSearchParameters, PagingQueryParams queryParams) {
        return this.eventService.findEventByParameters(eventSearchParameters, queryParams);
    }

}
