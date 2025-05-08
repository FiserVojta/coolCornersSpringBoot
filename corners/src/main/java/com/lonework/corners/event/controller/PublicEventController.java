package com.lonework.corners.event.controller;

import com.lonework.corners.event.model.DTO.EventCreateRequest;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.event.model.EventSearchParameters;
import com.lonework.corners.event.service.EventService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public Iterable<Event> findTrip(@ModelAttribute EventSearchParameters eventSearchParameters) {
        return this.eventService.findEventByParameters(eventSearchParameters);
    }

}
