package com.lonework.corners.wander.controller;

import com.lonework.corners.place.model.DTO.PlaceCreateRequest;
import com.lonework.corners.trip.model.TripCommentRequest;
import com.lonework.corners.user.model.User;
import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderCreateRequest;
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

    @CrossOrigin(origins = "*")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "")
    @PermitAll
    public ResponseEntity<Wander> createWander(@RequestBody WanderCreateRequest wanderCreateRequest) {

        return ResponseEntity.ok(wanderService.createWander(wanderCreateRequest, "test"));
    }

    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<Wander> getWander(@PathVariable("id") Long id) {

        return ResponseEntity.ok(wanderService.getWander(id));
    }

    @GetMapping("")
    @PermitAll
    public ResponseEntity<List<Wander>> getAllWanders() {

        return ResponseEntity.ok(wanderService.getAllWanders());
    }


}
