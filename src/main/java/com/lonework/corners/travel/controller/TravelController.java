package com.lonework.corners.travel.controller;

import com.lonework.corners.travel.model.TravelCreateRequest;
import com.lonework.corners.travel.model.TravelDetailResponse;
import com.lonework.corners.travel.model.TravelSummaryResponse;
import com.lonework.corners.travel.model.TravelVisibilityRequest;
import jakarta.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/travels")
public class TravelController {

    @Inject
    TravelFacade travelFacade;

    @PostMapping("")
    public ResponseEntity<TravelDetailResponse> createTravel(
            @RequestBody TravelCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(travelFacade.createTravel(request, jwt.getClaimAsString("email")));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TravelSummaryResponse>> getMyTravels(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(travelFacade.getMyTravels(jwt.getClaimAsString("email")));
    }

    @GetMapping("/{travelId}")
    public ResponseEntity<TravelDetailResponse> getTravel(
            @PathVariable Long travelId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(travelFacade.getTravel(travelId, jwt.getClaimAsString("email")));
    }

    @PutMapping("/{travelId}")
    @PreAuthorize("hasRole('ADMIN') or @travelSecurity.isOwner(#travelId, authentication.token.claims['email'])")
    public ResponseEntity<TravelDetailResponse> updateTravel(
            @PathVariable Long travelId,
            @RequestBody TravelCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(travelFacade.updateTravel(travelId, request, jwt.getClaimAsString("email")));
    }

    @PatchMapping("/{travelId}/visibility")
    @PreAuthorize("hasRole('ADMIN') or @travelSecurity.isOwner(#travelId, authentication.token.claims['email'])")
    public ResponseEntity<TravelDetailResponse> updateVisibility(
            @PathVariable Long travelId,
            @RequestBody TravelVisibilityRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(travelFacade.updateVisibility(travelId, request.visibility(), jwt.getClaimAsString("email")));
    }

    @DeleteMapping("/{travelId}")
    @PreAuthorize("hasRole('ADMIN') or @travelSecurity.isOwner(#travelId, authentication.token.claims['email'])")
    public ResponseEntity<Void> deleteTravel(@PathVariable Long travelId) {
        travelFacade.deleteTravel(travelId);
        return ResponseEntity.noContent().build();
    }
}
