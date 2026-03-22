package com.lonework.corners.place.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.place.model.DTO.PlaceSearchRequest;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.place.model.PlaceDetailResponse;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/public/places")
public class PublicPlaceController {

    @Autowired
    private PlaceFacade placeFacade;

    @GetMapping("/{id}")
    @PermitAll
    public PlaceDetailResponse getPlaceById(@PathVariable("id") Long id) {
        return placeFacade.getPlaceResponse(id);
    }

    @GetMapping()
    @PermitAll
    public PagedResult<Place> fetchPlace(PlaceSearchRequest placeSearchRequest, PagingQueryParams pagingQueryParams) {
        return placeFacade.findPlacesByParameters(placeSearchRequest, pagingQueryParams);

    }



}
