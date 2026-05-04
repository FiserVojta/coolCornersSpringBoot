package com.lonework.corners.user.controller;

import com.lonework.corners.user.model.User;
import com.lonework.corners.user.model.UserFollowRequest;
import com.lonework.corners.user.model.UserUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/{email}")
    public ResponseEntity<User> getUser(@PathVariable("email") String email) {

        return ResponseEntity.ok(userFacade.getUser(email));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userFacade.getUser(jwt.getClaimAsString("email")));
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMe(@AuthenticationPrincipal Jwt jwt, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userFacade.updateUser(jwt.getClaimAsString("email"), request));
    }


    @PostMapping("/follow")
    public ResponseEntity<User> followUser(@AuthenticationPrincipal Jwt jwt, @RequestBody UserFollowRequest request)
    {
        return ResponseEntity.ok(userFacade.followUser(jwt.getClaimAsString("email"), request));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<User> unfollowUser(@AuthenticationPrincipal Jwt jwt, @RequestBody UserFollowRequest request)
    {
        return ResponseEntity.ok(userFacade.unFollowUser(jwt.getClaimAsString("email"), request));
    }

}
