package com.lonework.corners.user.controller;

import com.lonework.corners.user.model.MeResponse;
import com.lonework.corners.user.model.User;
import com.lonework.corners.user.model.UserFollowRequest;
import com.lonework.corners.user.model.UserRateRequest;
import com.lonework.corners.user.model.UserUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserFacade userFacade;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userFacade.getUser(id));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(new MeResponse(userFacade.getUser(jwt.getClaimAsString("email"))));
    }

    @PutMapping("/me")
    public ResponseEntity<MeResponse> updateMe(@AuthenticationPrincipal Jwt jwt, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(new MeResponse(userFacade.updateUser(jwt.getClaimAsString("email"), request)));
    }


    @PostMapping("/follow")
    public ResponseEntity<MeResponse> followUser(@AuthenticationPrincipal Jwt jwt, @RequestBody UserFollowRequest request)
    {
        return ResponseEntity.ok(new MeResponse(userFacade.followUser(jwt.getClaimAsString("email"), request)));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<MeResponse> unfollowUser(@AuthenticationPrincipal Jwt jwt, @RequestBody UserFollowRequest request)
    {
        return ResponseEntity.ok(new MeResponse(userFacade.unFollowUser(jwt.getClaimAsString("email"), request)));
    }

    @PatchMapping("/{userId}/rate")
    public ResponseEntity<Double> rateUser(@PathVariable Long userId, @RequestBody UserRateRequest request) {
        return ResponseEntity.ok(userFacade.rateUser(request, userId));
    }

}
