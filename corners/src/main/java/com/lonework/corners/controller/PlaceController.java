package com.lonework.corners.controller;

import com.lonework.corners.model.Comment;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.services.PlaceService;
import com.lonework.corners.model.Place;
import com.lonework.corners.model.request.CommentCreateRequest;
import com.lonework.corners.model.request.PlaceCreateRequest;
import com.lonework.corners.model.request.PlaceSearchRequest;

@RestController
@RequestMapping("/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    public Place getPlaceById(@PathVariable("id") Long id) {

        return this.placeService.getPlaceById(id);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/create")
    public Place createPlace(@RequestBody PlaceCreateRequest placeRequest) {
        Place place = placeService.createPlace(placeRequest);
        return place;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/filter")
    public Iterable<Place> fetchPlace(PlaceSearchRequest placeSearchRequest) {
        Iterable<Place> savedPlaces = placeService.findPlacesByParametrs(placeSearchRequest);
        return savedPlaces;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/comment/{placeId}")
    public Comment createComment(@RequestBody CommentCreateRequest commentCreateRequest,
            @PathVariable("placeId") Long placeId) {
        return placeService.createComment(commentCreateRequest, placeId);
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
