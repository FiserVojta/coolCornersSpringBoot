package com.lonework.corners.user.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.user.model.User;
import com.lonework.corners.user.model.UserDetailResponse;
import com.lonework.corners.user.model.UserFollowRequest;
import com.lonework.corners.user.model.UserListResponse;
import com.lonework.corners.user.service.UserService;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;


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
    public User followUser(String myEmail, UserFollowRequest userFollowRequest) {
        var user = userService.getUser(myEmail);
        for (var userId : userFollowRequest.userIds()) {
            var userFriend = userService.getUser(userId);
            if(user == userFriend) {
                throw new RuntimeException("Can't follow this yourself");
            }
            user.addFollower(userFriend);
            saveUser(userFriend);
        }
        saveUser(user);
        return user;
    }

    @Transactional
    public User unFollowUser(String myEmail, UserFollowRequest userFollowRequest) {
        var user = userService.getUser(myEmail);
        for (var userId : userFollowRequest.userIds()) {
            var userFriend = userService.getUser(userId);
            if(user == userFriend) {
                throw new RuntimeException("Can't follow this yourself");
            }
            user.removeFollower(userFriend);
            saveUser(userFriend);
        }
        saveUser(user);
        return user;
    }

    public void saveUser(User user) {
        entityManager.merge(user);
    }
}
