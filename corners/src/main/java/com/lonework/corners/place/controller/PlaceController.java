package com.lonework.corners.place.controller;

import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.place.model.PlaceDetailResponse;
import com.lonework.corners.place.model.PlaceRateRequest;
import com.lonework.corners.trip.model.TripRateRequest;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.place.services.PlaceService;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.comment.model.CommentCreateRequest;
import com.lonework.corners.place.model.PlaceCreateRequest;
import com.lonework.corners.place.model.PlaceSearchRequest;

@RestController
@RequestMapping("/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    public PlaceDetailResponse getPlaceById(@PathVariable("id") Long id) {
        return this.placeService.getPlaceResponse(id);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/create")
    public Place createPlace(@RequestBody PlaceCreateRequest placeRequest) {
        return placeService.createPlace(placeRequest);
    }

    @CrossOrigin(origins = "*")
    @GetMapping()
    public Iterable<Place> fetchPlace(PlaceSearchRequest placeSearchRequest) {
        return placeService.findPlacesByParameters(placeSearchRequest);

    }

    @CrossOrigin(origins = "*")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/{placeId}/comment")
    public Comment createComment(@RequestBody CommentCreateRequest commentCreateRequest,
            @PathVariable("placeId") Long placeId) {
        return placeService.createComment(commentCreateRequest, placeId);
    }

    @CrossOrigin(origins = "*")
    @PatchMapping("/{placeId}/rate")
    public void rateTrip(@RequestBody PlaceRateRequest placeRateRequest, @PathVariable Long placeId) {
        this.placeService.ratePlace(placeRateRequest, placeId);
        new Response().setMessage("Trip rated successfully");
    }

//    @CrossOrigin(origins = "/v1/*")
//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "")
//    public ResponseEntity<String> createPlace(@RequestBody PlaceCreateRequest feature) {
//        // Process the feature as needed, e.g., save to database
//        // For simplicity, we are just returning the feature as a GeoJSON string
//
//
//
//        return new ResponseEntity<>(null, HttpStatus.CREATED);
//    }

}
