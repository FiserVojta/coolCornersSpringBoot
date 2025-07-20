package com.lonework.corners.wander.controller;

import com.lonework.corners.place.model.DTO.PlaceCreateRequest;
import com.lonework.corners.trip.model.TripCommentRequest;
import com.lonework.corners.user.model.User;
import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderCreateRequest;
import com.lonework.corners.wander.model.WanderDetailResponse;
import com.lonework.corners.wander.model.WanderListResponse;
import com.lonework.corners.wander.service.WanderService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/public/wanders")
public class WanderPublicController {

    @Inject
    WanderService wanderService;

    @Inject
    WanderFacade wanderFacade;

    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<WanderDetailResponse> getWander(@PathVariable("id") Long id) {
        return ResponseEntity.ok(wanderFacade.getWander(id));
    }

    @GetMapping("")
    @PermitAll
    public ResponseEntity<List<WanderListResponse>> getAllWanders() {
        return ResponseEntity.ok(wanderFacade.getAllWanders());
    }


}
