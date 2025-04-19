package com.lonework.corners.trip.controller;

import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.place.model.PlaceListRequest;
import com.lonework.corners.trip.model.TripCreateRequest;
import com.lonework.corners.trip.model.TripSearchRequest;
import com.lonework.corners.trip.model.TripDetailResponse;
import com.lonework.corners.trip.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping("/trip")
public class TripController {

    @Autowired
    private TripService tripService;

    @CrossOrigin(origins = "*")
    @PostMapping("")
    public Trip createTrip(@RequestBody TripCreateRequest tripRequest) {
        return tripService.crateTrip(tripRequest);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    public TripDetailResponse findTripById(@PathVariable Long id) {
        return this.tripService.findTripById(id);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/add-places/{id}")
    public Optional<Trip> addPlacesToTrip(@RequestBody PlaceListRequest placeListRequest, @PathVariable Long id) {

        return this.tripService.addPlacesToTrip(placeListRequest, id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/find")
    public Iterable<Trip> findTrip(@ModelAttribute TripSearchRequest tripSearchRequest) {
        return this.tripService.findTripByParameters(tripSearchRequest);
    }
}
