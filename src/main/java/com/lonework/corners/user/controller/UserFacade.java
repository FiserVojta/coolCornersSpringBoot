package com.lonework.corners.user.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.user.model.User;
import com.lonework.corners.user.model.UserDetailResponse;
import com.lonework.corners.user.model.UserListResponse;
import com.lonework.corners.user.service.UserService;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;


@ApplicationScope
@Service
public class UserFacade {

    @Inject
    UserService userService;

    @Inject
    EntityManager entityManager;

    public PagedResult<UserListResponse> getUserList(PagingQueryParams queryParams) {
        return userService.getUserList(queryParams);
    }

    public UserDetailResponse getUserDetail(String email) {
        return userService.getUserDetail(email);
    }


    @Transactional
    public void followUser(String myEmail, Long userId ) {
        var user = userService.getUser(myEmail);
        var userFriend = userService.getUser(userId);

        user.addFriend(userFriend);

        saveUser(user);
        saveUser(userFriend);
    }

    public void saveUser(User user) {
        entityManager.merge(user);
    }
}
