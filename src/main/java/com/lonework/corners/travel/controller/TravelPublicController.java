package com.lonework.corners.travel.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.travel.model.TravelDetailResponse;
import com.lonework.corners.travel.model.TravelSummaryResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/public/travels")
public class TravelPublicController {

    @Inject
    TravelFacade travelFacade;

    @GetMapping("")
    @PermitAll
    public ResponseEntity<PagedResult<TravelSummaryResponse>> getPublicTravels(PagingQueryParams queryParams) {
        return ResponseEntity.ok(travelFacade.getPublicTravels(queryParams));
    }

    /**
     * Travels the current viewer may see. Anonymous visitors get PUBLIC travels;
     * a valid Bearer token (still parsed on permitAll routes) additionally unlocks
     * the viewer's own travels and FOLLOWERS travels of owners they follow.
     */
    @GetMapping("/accessible")
    @PermitAll
    public ResponseEntity<List<TravelSummaryResponse>> getAccessibleTravels(@AuthenticationPrincipal Jwt jwt) {
        String viewerEmail = jwt != null ? jwt.getClaimAsString("email") : null;
        return ResponseEntity.ok(travelFacade.getTravelsForViewer(viewerEmail));
    }

    @GetMapping("/share/{token}")
    @PermitAll
    public ResponseEntity<TravelDetailResponse> getSharedTravel(@PathVariable("token") String token) {
        return ResponseEntity.ok(travelFacade.getSharedTravel(token));
    }

    /**
     * A user's travels visible to the current viewer: PUBLIC ones for anonymous
     * visitors; a valid Bearer token additionally unlocks the viewer's own travels
     * and FOLLOWERS travels when the viewer follows that user.
     */
    @GetMapping("/user/{userId}")
    @PermitAll
    public ResponseEntity<List<TravelSummaryResponse>> getUserTravels(
            @PathVariable Long userId,
            @AuthenticationPrincipal Jwt jwt) {
        String viewerEmail = jwt != null ? jwt.getClaimAsString("email") : null;
        return ResponseEntity.ok(travelFacade.getUserTravels(userId, viewerEmail));
    }

    /**
     * A single travel, enforcing the same visibility rules as the accessible list:
     * anonymous visitors see PUBLIC travels only; a valid Bearer token additionally
     * unlocks the viewer's own and followed users' travels. Hidden travels 404.
     */
    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<TravelDetailResponse> getTravel(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Jwt jwt) {
        String viewerEmail = jwt != null ? jwt.getClaimAsString("email") : null;
        return ResponseEntity.ok(travelFacade.getTravel(id, viewerEmail));
    }
}
