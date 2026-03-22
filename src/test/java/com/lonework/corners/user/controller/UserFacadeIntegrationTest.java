package com.lonework.corners.user.controller;

import com.lonework.corners.user.model.User;
import com.lonework.corners.user.model.UserDetailResponse;
import com.lonework.corners.user.model.UserFollowRequest;
import com.lonework.corners.user.model.UserListResponse;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class UserFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private UserFacade userFacade;

    @Test
    void getUserListReturnsPersistedUsers() {
        createUser("anna@example.com", "Anna");
        createUser("bob@example.com", "Bob");
        flushAndClear();

        List<UserListResponse> users = userFacade.getUserList(createPagingQueryParams()).data.stream()
                .sorted(Comparator.comparing(UserListResponse::email))
                .toList();

        assertEquals(2, users.size());
        assertEquals(List.of("anna@example.com", "bob@example.com"), users.stream().map(UserListResponse::email).toList());
    }

    @Test
    void getUserAndGetUserDetailReturnPersistedUser() {
        User user = createUser("detail@example.com", "Detail User");
        flushAndClear();

        User foundUser = userFacade.getUser("detail@example.com");
        UserDetailResponse detail = userFacade.getUserDetail("detail@example.com");

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        assertEquals("detail@example.com", detail.email());
        assertEquals("Detail User", detail.displayName());
    }

    @Test
    void followAndUnfollowUserPersistRelationship() {
        User me = createUser("me@example.com", "Me");
        User target = createUser("target@example.com", "Target");
        flushAndClear();

        userFacade.followUser("me@example.com", new UserFollowRequest(List.of(target.getId())));
        flushAndClear();

        User followedUser = entityManager.find(User.class, me.getId());
        assertEquals(1, followedUser.getFollowers().size());

        userFacade.unFollowUser("me@example.com", new UserFollowRequest(List.of(target.getId())));
        flushAndClear();

        User unfollowedUser = entityManager.find(User.class, me.getId());
        assertEquals(0, unfollowedUser.getFollowers().size());
    }

    @Test
    void getUsersByIdsThrowsWhenAnyUserIsMissing() {
        User user = createUser("existing@example.com", "Existing");
        flushAndClear();

        assertThrows(EntityNotFoundException.class, () -> userFacade.getUsersByIds(List.of(user.getId(), Long.MAX_VALUE)));
    }
}
