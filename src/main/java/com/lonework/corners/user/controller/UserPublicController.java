package com.lonework.corners.user.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.user.model.UserDetailResponse;
import com.lonework.corners.user.model.UserListResponse;
import com.lonework.corners.user.model.UserSearchParameters;
import jakarta.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @GetMapping("/{email}/trips")
    public ResponseEntity<List<Trip>> getUserTrips(@PathVariable("email") String email) {
        return ResponseEntity.ok(userFacade.getUserTrips(email));
    }

    @GetMapping("")
    public ResponseEntity<PagedResult<UserListResponse>> getUserList(@ModelAttribute UserSearchParameters userSearchParameters,
                                                                     PagingQueryParams queryParams) {
        return ResponseEntity.ok(userFacade.getUserList(userSearchParameters, queryParams));
    }
}
