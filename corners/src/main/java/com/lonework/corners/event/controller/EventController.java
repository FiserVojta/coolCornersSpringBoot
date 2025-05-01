package com.lonework.corners.event.controller;

import com.lonework.corners.event.model.DTO.EventCreateRequest;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.event.model.EventSearchParameters;
import com.lonework.corners.event.service.EventService;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.trip.model.TripSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public Event createEvent(@RequestBody EventCreateRequest eventCreateRequest) {
        return eventService.createEvent(eventCreateRequest);
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
}
