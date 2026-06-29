package com.lonework.corners.travel.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.travel.model.TravelDetailResponse;
import com.lonework.corners.travel.model.TravelSummaryResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/public/travels")
public class TravelPublicController {

    @Inject
    TravelFacade travelFacade;

    @GetMapping("")
    @PermitAll
    public ResponseEntity<PagedResult<TravelSummaryResponse>> getPublicTravels(PagingQueryParams queryParams) {
        return ResponseEntity.ok(travelFacade.getPublicTravels(queryParams));
    }

    @GetMapping("/share/{token}")
    @PermitAll
    public ResponseEntity<TravelDetailResponse> getSharedTravel(@PathVariable("token") String token) {
        return ResponseEntity.ok(travelFacade.getSharedTravel(token));
    }

    @GetMapping("/user/{userId}")
    @PermitAll
    public ResponseEntity<List<TravelSummaryResponse>> getPublicUserTravels(@PathVariable Long userId) {
        return ResponseEntity.ok(travelFacade.getPublicUserTravels(userId));
    }

    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<TravelDetailResponse> getPublicTravel(@PathVariable("id") Long id) {
        return ResponseEntity.ok(travelFacade.getPublicTravel(id));
    }
}
