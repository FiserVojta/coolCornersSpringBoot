package com.lonework.corners.event.controller;

import com.lonework.corners.event.model.DTO.EventCreateRequest;
import com.lonework.corners.event.model.Event;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private EventFacade eventFacade;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "")
    @PermitAll
    public Event createEvent(@RequestBody EventCreateRequest eventCreateRequest, @AuthenticationPrincipal Jwt jwt) {
        return eventFacade.createEvent(eventCreateRequest, jwt.getClaimAsString("email"));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or @eventSecurity.isOwner(#id, authentication.token.claims['email'])")
    public Event updateEvent(@PathVariable("id") Long id, @RequestBody EventCreateRequest eventCreateRequest) {
        return eventFacade.updateEvent(eventCreateRequest, id);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or @eventSecurity.isOwner(#id, authentication.token.claims['email'])")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        eventFacade.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

}
