package com.lonework.corners.trip.controller;

import com.lonework.corners.place.model.DTO.PlaceListRequest;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.trip.model.TripCommentRequest;
import com.lonework.corners.trip.model.TripCreateRequest;
import com.lonework.corners.trip.model.TripRateRequest;
import com.lonework.corners.trip.services.TripService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @CrossOrigin(origins = "*")
    @PostMapping("")
    public Trip createTrip(@RequestBody TripCreateRequest tripRequest) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            return tripService.crateTrip(tripRequest, email);
        }
        throw new RuntimeException("JWT token not found or invalid");

    }

    @CrossOrigin(origins = "*")
    @PostMapping("/add-places/{id}")
    public Optional<Trip> addPlacesToTrip(@RequestBody PlaceListRequest placeListRequest, @PathVariable Long id) {

        return this.tripService.addPlacesToTrip(placeListRequest, id);
    }

    @CrossOrigin(origins = "*")
    @PatchMapping("/{tripId}/rate")
    public void rateTrip(@RequestBody TripRateRequest tripRateRequest, @PathVariable Long tripId) {
        this.tripService.rateTrip(tripRateRequest, tripId);
        new Response().setMessage("Trip rated successfully");
    }

    @CrossOrigin(origins = "*")
    @PatchMapping("/{tripId}/comment")
    public void commentTrip(@RequestBody TripCommentRequest tripCommentRequest, @PathVariable Long tripId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            this.tripService.commentTrip(tripCommentRequest, tripId, email);
            return;
        }
        throw new RuntimeException("JWT token not found or invalid");
    }
}
