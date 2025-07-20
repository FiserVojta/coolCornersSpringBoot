package com.lonework.corners.user.controller;

import com.lonework.corners.user.model.UserDetailResponse;
import com.lonework.corners.user.model.UserListResponse;
import jakarta.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/public/users")
public class UserPublicController {

    @Inject
    UserFacade userFacade;

    @GetMapping("/{email}")
    public ResponseEntity<UserDetailResponse> getUser(@PathVariable("email") String email) {
        return ResponseEntity.ok(userFacade.getUserDetail(email));
    }

    @GetMapping("")
    public ResponseEntity<List<UserListResponse>> getUserList() {
        return ResponseEntity.ok(userFacade.getUserList());
    }
}