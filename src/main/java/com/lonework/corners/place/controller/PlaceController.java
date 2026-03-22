package com.lonework.corners.place.controller;

import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.place.model.DTO.PlaceRateRequest;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.place.model.Place;
import com.lonework.corners.comment.model.CommentCreateRequest;
import com.lonework.corners.place.model.DTO.PlaceCreateRequest;


@RestController
@RequestMapping("/places")
public class PlaceController {

    @Autowired
    private PlaceFacade placeFacade;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "")
    public Place createPlace(@RequestBody PlaceCreateRequest placeRequest) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            return placeFacade.createPlace(placeRequest, email);
        }
        throw new RuntimeException("JWT token not found or invalid");
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/{placeId}")
    public Place updatePlace(@RequestBody PlaceCreateRequest placeRequest, @PathVariable("placeId") Long placeId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            return placeFacade.updatePlace(placeRequest, email, placeId);
        }
        throw new RuntimeException("JWT token not found or invalid");
    }



    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/{placeId}/comment")
    public Comment commentPlace(@RequestBody CommentCreateRequest commentCreateRequest,
            @PathVariable("placeId") Long placeId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            return placeFacade.commentPlace(commentCreateRequest, placeId, email);
        }

        throw new RuntimeException("JWT token not found or invalid");

    }

    @PatchMapping("/{placeId}/rate")
    public void ratePlace(@RequestBody PlaceRateRequest placeRateRequest, @PathVariable Long placeId) {
        placeFacade.ratePlace(placeRateRequest, placeId);
        new Response().setMessage("Place rated successfully");
    }


}
