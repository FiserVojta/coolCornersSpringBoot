package com.lonework.corners.user.controller;

import com.lonework.corners.user.model.User;
import com.lonework.corners.user.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping("/users")
public class UserController {

    @Inject
    UserService userService;

    @GetMapping("/{email}")
    public ResponseEntity<User> getUser(@PathVariable("email") String email) {

        return ResponseEntity.ok(userService.getUser(email));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getUser(jwt.getClaimAsString(jwt.getClaimAsString("email"))));
    }

}