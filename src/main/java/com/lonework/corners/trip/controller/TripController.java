package com.lonework.corners.trip.controller;

import com.lonework.corners.place.model.DTO.PlaceListRequest;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.trip.model.DTO.TripCommentRequest;
import com.lonework.corners.trip.model.DTO.TripCreateRequest;
import com.lonework.corners.trip.model.DTO.TripDetailResponse;
import com.lonework.corners.trip.model.DTO.TripRateRequest;
import com.lonework.corners.trip.model.DTO.TripUpdateRequest;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripFacade tripFacade;

    @PostMapping("")
    public Trip createTrip(@RequestBody TripCreateRequest tripRequest) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            return tripFacade.createTrip(tripRequest, email);
        }
        throw new RuntimeException("JWT token not found or invalid");

    }

    @PostMapping("/add-places/{id}")
    @PreAuthorize("hasRole('ADMIN') or @tripSecurity.isOwner(#id, authentication.token.claims['email'])")
    public Optional<Trip> addPlacesToTrip(@RequestBody PlaceListRequest placeListRequest, @PathVariable Long id) {

        return tripFacade.addPlacesToTrip(placeListRequest, id);
    }

    @PatchMapping("/{tripId}/rate")
    public void rateTrip(@RequestBody TripRateRequest tripRateRequest, @PathVariable Long tripId) {
        tripFacade.rateTrip(tripRateRequest, tripId);
        new Response().setMessage("Trip rated successfully");
    }

    @PatchMapping("/{tripId}/comment")
    public void commentTrip(@RequestBody TripCommentRequest tripCommentRequest, @PathVariable Long tripId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            tripFacade.commentTrip(tripCommentRequest, tripId, jwt.getClaimAsString("email"));
            return;
        }
        throw new RuntimeException("JWT token not found or invalid");
    }

    @PutMapping("/{tripId}")
    @PreAuthorize("hasRole('ADMIN') or @tripSecurity.isOwner(#tripId, authentication.token.claims['email'])")
    public void updateTrip(@RequestBody TripUpdateRequest tripUpdateRequest, @PathVariable Long tripId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            tripFacade.updateTrip(tripUpdateRequest, tripId, jwt.getClaimAsString("email"));
            return;
        }
        throw new RuntimeException("JWT token not found or invalid");
    }

    @PostMapping("/{tripId}/done")
    public ResponseEntity<TripDetailResponse> markTripDone(@PathVariable Long tripId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return ResponseEntity.ok(tripFacade.markTripDone(tripId, jwt.getClaimAsString("email")));
        }
        throw new RuntimeException("JWT token not found or invalid");
    }
}
