package com.lonework.corners.wander.controller;

import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderCreateRequest;
import com.lonework.corners.wander.service.WanderService;
import jakarta.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/wanders")
public class WanderController {

    @Inject
    WanderService wanderService;

    @PostMapping("")
    public ResponseEntity<Wander> createWander(@RequestBody WanderCreateRequest wanderCreateRequest, @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(wanderService.createWander(wanderCreateRequest, jwt.getClaimAsString("email")));
    }

}