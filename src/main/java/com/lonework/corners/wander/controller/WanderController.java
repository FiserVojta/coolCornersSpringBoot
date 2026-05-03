package com.lonework.corners.wander.controller;

import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderCreateRequest;
import jakarta.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/wanders")
public class WanderController {

    @Inject
    WanderFacade wanderFacade;

    @PostMapping("")
    public ResponseEntity<Wander> createWander(@RequestBody WanderCreateRequest wanderCreateRequest, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(wanderFacade.createWander(wanderCreateRequest, jwt.getClaimAsString("email")));
    }
    
    @PutMapping("/{wanderId}")
    @PreAuthorize("hasRole('ADMIN') or @wanderSecurity.isOwner(#wanderId, authentication.token.claims['email'])")
    public ResponseEntity<Wander> updateWander(
            @PathVariable Long wanderId,
            @RequestBody WanderCreateRequest wanderCreateRequest) {
        return ResponseEntity.ok(wanderFacade.updateWander(wanderId, wanderCreateRequest));
    }
    
    @PostMapping("/{wanderId}/join")
    public ResponseEntity<Wander> joinWander(@PathVariable Long wanderId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(wanderFacade.joinWander(wanderId, jwt.getClaimAsString("email")));
    }
    
    @PostMapping("/{wanderId}/leave")
    public ResponseEntity<Wander> leaveWander(@PathVariable Long wanderId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(wanderFacade.leaveWander(wanderId, jwt.getClaimAsString("email")));
    }
    
    @DeleteMapping("/{wanderId}")
    @PreAuthorize("hasRole('ADMIN') or @wanderSecurity.isOwner(#wanderId, authentication.token.claims['email'])")
    public ResponseEntity<Void> deleteWander(@PathVariable Long wanderId) {
        wanderFacade.deleteWander(wanderId);
        return ResponseEntity.noContent().build();
    }
}
