package com.lonework.corners.trip.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.trip.model.DTO.TripDetailResponse;
import com.lonework.corners.trip.model.TripSearchRequest;
import com.lonework.corners.trip.services.TripService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/public/trips")
public class PublicTripContoroler {


    @Autowired
    private TripService tripService;

    @GetMapping("/{id}")
    @PermitAll
    public TripDetailResponse findTripById(@PathVariable Long id) {
        return this.tripService.findTripById(id);
    }

    @GetMapping("")
    @PermitAll
    public PagedResult<Trip> findTrip(@RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) List<Long> tags,
                                      PagingQueryParams queryParams) {
        return this.tripService.findTripByParameters(new TripSearchRequest(categories, tags, ""), queryParams);
    }
}



