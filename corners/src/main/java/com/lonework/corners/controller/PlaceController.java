package com.lonework.corners.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.repository.PlaceRepository;
import com.lonework.corners.services.PlaceService;

import com.lonework.corners.model.Place;
import com.lonework.corners.model.request.PlaceSearchRequest;

@RestController
@RequestMapping("/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    public Place getPlaceById(@PathVariable("id") Long id) {

        return this.placeService.getPlaceById(id);
    }

    @CrossOrigin(origins = "*")
    @PostMapping()
    public ResponseEntity<Place> createPlace(@RequestBody Place place) {
        Place savedPlace = placeService.createPlace(place);
        return ResponseEntity.ok(savedPlace);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/fetch")
    public Place fetchPlace(@RequestBody PlaceSearchRequest placeSearchRequest) {
        Place savedPlace = placeService.findPlacesByParametrs(placeSearchRequest);
        return savedPlace;
    }
}
