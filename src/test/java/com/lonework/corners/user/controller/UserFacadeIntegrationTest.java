package com.lonework.corners.user.controller;

import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.user.model.User;
import com.lonework.corners.user.model.UserDetailResponse;
import com.lonework.corners.user.model.UserFollowRequest;
import com.lonework.corners.user.model.UserListResponse;
import com.lonework.corners.user.model.UserSearchParameters;
import com.lonework.corners.trip.controller.TripFacade;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategoryType;
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

    @Autowired
    private TripFacade tripFacade;

    @Test
    void getUserListReturnsPersistedUsers() {
        createUser("anna@example.com", "Anna");
        createUser("bob@example.com", "Bob");
        flushAndClear();

        List<UserListResponse> users = userFacade.getUserList(new UserSearchParameters(null, null), createPagingQueryParams()).data.stream()
                .sorted(Comparator.comparing(UserListResponse::email))
                .toList();

        assertEquals(2, users.size());
        assertEquals(List.of("anna@example.com", "bob@example.com"), users.stream().map(UserListResponse::email).toList());
    }

    @Test
    void getUserListFiltersUsersBySearchText() {
        createUser("anna@example.com", "Anna");
        createUser("bob@example.com", "Bob");
        createUser("annika@example.com", "Annika");
        flushAndClear();

        UserSearchParameters userSearchParameters = new UserSearchParameters("ann", null);

        var result = userFacade.getUserList(userSearchParameters, createPagingQueryParams());
        List<UserListResponse> users = result.data.stream()
                .sorted(Comparator.comparing(UserListResponse::email))
                .toList();

        assertEquals(2, result.totalItems);
        assertEquals(List.of("anna@example.com", "annika@example.com"), users.stream().map(UserListResponse::email).toList());
    }

    @Test
    void getUserListUsesPageNumberAsOffsetMultiplier() {
        createUser("anna@example.com", "Anna");
        createUser("bob@example.com", "Bob");
        createUser("cara@example.com", "Cara");
        createUser("david@example.com", "David");
        createUser("eva@example.com", "Eva");
        flushAndClear();

        var result = userFacade.getUserList(new UserSearchParameters(null, null), new PagingQueryParams(1, 2, null, null));

        assertEquals(5, result.totalItems);
        assertEquals(List.of("cara@example.com", "david@example.com"), result.data.stream().map(UserListResponse::email).toList());
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
    void getUserTripsReturnsTripsMarkedDoneByUser() {
        User user = createUser("detail@example.com", "Detail User");
        Category category = createCategory("Trip Category", CategoryType.TRIP);
        Trip completedTrip = createTrip("Completed Trip", category, List.of(), List.of(), "integration@example.com");
        Trip otherTrip = createTrip("Other Trip", category, List.of(), List.of(), "integration@example.com");
        flushAndClear();

        tripFacade.markTripDone(completedTrip.getId(), user.getEmail());
        flushAndClear();

        List<Trip> trips = userFacade.getUserTrips(user.getEmail()).stream()
                .sorted(Comparator.comparing(Trip::getName))
                .toList();

        assertEquals(1, trips.size());
        assertEquals(completedTrip.getId(), trips.getFirst().getId());
        assertEquals("Completed Trip", trips.getFirst().getName());
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
