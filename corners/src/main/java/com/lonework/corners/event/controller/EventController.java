package com.lonework.corners.event.controller;

import com.lonework.corners.event.model.DTO.EventCreateRequest;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.event.model.EventSearchParameters;
import com.lonework.corners.event.service.EventService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
@RequestMapping("/events")
public class EventController {

    @Autowired
    EventService eventService;

    @CrossOrigin(origins = "*")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "")
    @PermitAll
    public Event createEvent(@RequestBody EventCreateRequest eventCreateRequest) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            return eventService.createEvent(eventCreateRequest, email);
        }

        throw new RuntimeException("JWT token not found or invalid");
    }

    @CrossOrigin(origins = "*")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/{id}")
    public Event updateEvent(@PathVariable("id") Long id, @RequestBody EventCreateRequest eventCreateRequest) {
        return eventService.updatedEvent(eventCreateRequest, id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/{id}")
    public Event getEvent(@PathVariable("id") Long id) {
        return eventService.getEvent(id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/find")
    public Iterable<Event> findTrip(@ModelAttribute EventSearchParameters eventSearchParameters) {
        return this.eventService.findEventByParameters(eventSearchParameters);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

}
