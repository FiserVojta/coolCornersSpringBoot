package com.lonework.corners.user.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.user.model.User;
import com.lonework.corners.user.model.UserDetailResponse;
import com.lonework.corners.user.model.UserFollowRequest;
import com.lonework.corners.user.model.UserListResponse;
import com.lonework.corners.user.model.UserRateRequest;
import com.lonework.corners.user.model.UserSearchParameters;
import com.lonework.corners.user.model.UserUpdateRequest;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.user.service.UserService;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
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

    public PagedResult<UserListResponse> getUserList(UserSearchParameters userSearchParameters, PagingQueryParams queryParams) {
        return userService.getUserList(userSearchParameters, queryParams);
    }

    public UserDetailResponse getUserDetail(String email) {
        return userService.getUserDetail(email);
    }

    public User getUser(String email) {
        return userService.getUser(email);
    }

    public List<Trip> getUserTrips(String email) {
        return userService.getUserTrips(email);
    }

    public List<Place> getUserPlaces(String email) {
        return userService.getUserPlaces(email);
    }

    @Transactional
    public User updateUser(String email, UserUpdateRequest request) {
        return userService.updateUser(email, request);
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

    @Transactional
    public Double rateUser(UserRateRequest request, Long userId) {
        return userService.rateUser(request, userId);
    }

    public List<User> getUsersByIds(List<Long> ids) {
        List<User> users = userService.getUsersByIds(ids);
        if (users.size() != ids.size()) {
            throw new EntityNotFoundException("Some users were not found");
        }
        return users;
    }

    public void saveUser(User user) {
        entityManager.merge(user);
    }
}
