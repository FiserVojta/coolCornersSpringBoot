package com.lonework.corners.common.controller;

import jakarta.annotation.security.PermitAll;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping("/public/api/user")
public class UserController {

    @GetMapping("/profile")
    @PermitAll
    public ResponseEntity<String> profile(Principal principal) {
        String name = (principal != null) ? principal.getName() : "Guest";
        return ResponseEntity.ok("Hello, " + name);
    }

}