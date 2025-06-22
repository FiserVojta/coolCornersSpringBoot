package com.lonework.corners.user.controller;

import com.lonework.corners.user.model.User;
import com.lonework.corners.user.model.UserDetailResponse;
import com.lonework.corners.user.model.UserListlResponse;
import com.lonework.corners.user.service.UserService;
import jakarta.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/public/users")
public class UserPublicController {

    @Inject
    UserService userService;

    @GetMapping("/{email}")
    public ResponseEntity<UserDetailResponse> getUser(@PathVariable("email") String email) {
        return ResponseEntity.ok(userService.getUserDetail(email));
    }

    @GetMapping("")
    public ResponseEntity<List<UserListlResponse>> getUserList() {
        return ResponseEntity.ok(userService.getUserList());
    }
}