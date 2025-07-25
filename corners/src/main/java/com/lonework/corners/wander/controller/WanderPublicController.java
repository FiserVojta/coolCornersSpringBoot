package com.lonework.corners.wander.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.wander.model.WanderDetailResponse;
import com.lonework.corners.wander.model.WanderListResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/public/wanders")
public class WanderPublicController {

    @Inject
    WanderFacade wanderFacade;

    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<WanderDetailResponse> getWander(@PathVariable("id") Long id) {
        return ResponseEntity.ok(wanderFacade.getWander(id));
    }

    @GetMapping("")
    @PermitAll
    public ResponseEntity<PagedResult<WanderListResponse>> getAllWanders(PagingQueryParams queryParams) {
        return ResponseEntity.ok(wanderFacade.getWanderListResponse(queryParams));
    }
}
