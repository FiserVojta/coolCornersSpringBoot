package com.lonework.corners.place.controller;

import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.comment.model.CommentCreateRequest;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.place.model.DTO.PlaceCreateRequest;
import com.lonework.corners.place.model.DTO.PlaceRateRequest;
import com.lonework.corners.place.model.DTO.PlaceSearchRequest;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.place.model.PlaceDetailResponse;
import com.lonework.corners.place.services.PlaceService;
import jakarta.annotation.security.PermitAll;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/public/places")
public class PublicPlaceController {

    @Autowired
    private PlaceService placeService;

    @GetMapping("/{id}")
    @PermitAll
    public PlaceDetailResponse getPlaceById(@PathVariable("id") Long id) {
        return this.placeService.getPlaceResponse(id);
    }

    @GetMapping()
    @PermitAll
    public PagedResult<Place> fetchPlace(PlaceSearchRequest placeSearchRequest, PagingQueryParams pagingQueryParams) {
        return placeService.findPlacesByParameters(placeSearchRequest, pagingQueryParams);

    }



}
