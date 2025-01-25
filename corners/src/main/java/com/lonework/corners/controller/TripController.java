package com.lonework.corners.controller;

import com.lonework.corners.model.Trip;
import com.lonework.corners.model.request.PlaceListRequest;
import com.lonework.corners.model.request.TripCreateRequest;
import com.lonework.corners.model.request.TripSearchRequest;
import com.lonework.corners.services.TripService;
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
    public Trip CreateTrip(@RequestBody TripCreateRequest tripRequest) {

        return this.tripService.crateTrip(tripRequest);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    public Trip FindTripById(@PathVariable Long id) {

        return this.tripService.findTripById(id);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/add-places/{id}")
    public Optional<Trip> AddPlacesToTrip(@RequestBody PlaceListRequest placeListRequest, @PathVariable Long id) {

        return this.tripService.addPlacesToTrip(placeListRequest, id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/find")
    public Iterable<Trip> findTrip(@ModelAttribute TripSearchRequest tripSearchRequest) {

        return this.tripService.findTripByparameters(tripSearchRequest);
    }
}
