package com.lonework.corners.user.controller;

import com.lonework.corners.user.model.User;
import com.lonework.corners.user.model.UserDetailResponse;
import com.lonework.corners.user.model.UserListResponse;
import com.lonework.corners.user.service.UserService;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;


@ApplicationScope
@Service
public class UserFacade {

    @Inject
    UserService userService;

    public List<UserListResponse> getUserList() {
        return userService.getUserList();
    }

    public UserDetailResponse getUserDetail(String email) {
        return userService.getUserDetail(email);
    }
}
