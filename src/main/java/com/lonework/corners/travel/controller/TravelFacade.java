package com.lonework.corners.travel.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.travel.model.Travel;
import com.lonework.corners.travel.model.TravelCreateRequest;
import com.lonework.corners.travel.model.TravelDetailResponse;
import com.lonework.corners.travel.model.TravelSummaryResponse;
import com.lonework.corners.travel.model.TravelVersionResponse;
import com.lonework.corners.travel.model.TravelVisibility;
import com.lonework.corners.travel.service.TravelService;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;


@ApplicationScope
@Service
public class TravelFacade {

    @Inject
    TravelService travelService;

    public TravelDetailResponse createTravel(TravelCreateRequest request, String email) {
        Travel travel = travelService.createTravel(request, email);
        return TravelDetailResponse.from(travel, true);
    }

    public TravelDetailResponse updateTravel(Long travelId, TravelCreateRequest request, String email) {
        Travel travel = travelService.updateTravel(travelId, request);
        return TravelDetailResponse.from(travel, isOwner(travel, email));
    }

    public TravelDetailResponse updateVisibility(Long travelId, TravelVisibility visibility, String email) {
        Travel travel = travelService.updateVisibility(travelId, visibility);
        return TravelDetailResponse.from(travel, isOwner(travel, email));
    }

    public void deleteTravel(Long travelId) {
        travelService.deleteTravel(travelId);
    }

    public List<TravelSummaryResponse> getMyTravels(String email) {
        return travelService.getMyTravels(email).stream()
                .map(TravelSummaryResponse::from)
                .toList();
    }

    public List<TravelSummaryResponse> getTravelsForViewer(String viewerEmail) {
        return travelService.getTravelsForViewer(viewerEmail).stream()
                .map(TravelSummaryResponse::from)
                .toList();
    }

    public TravelDetailResponse getTravel(Long travelId, String viewerEmail) {
        Travel travel = travelService.getTravelForViewer(travelId, viewerEmail);
        return withVersions(travel, isOwner(travel, viewerEmail),
                travelService.getViewerRating(travelId, viewerEmail), viewerEmail);
    }

    public TravelDetailResponse rateTravel(Long travelId, Integer rating, String viewerEmail) {
        Travel travel = travelService.rateTravel(travelId, viewerEmail, rating);
        return withVersions(travel, isOwner(travel, viewerEmail),
                travelService.getViewerRating(travelId, viewerEmail), viewerEmail);
    }

    public TravelDetailResponse getSharedTravel(String shareToken) {
        // The link holder is anonymous, so they only see public versions of the same trip.
        return withVersions(travelService.getByShareToken(shareToken), false, null, null);
    }

    /** Detail response enriched with this trip's version group (how often it was done, by whom). */
    private TravelDetailResponse withVersions(Travel travel, boolean includeShareToken, Integer myRating,
                                              String viewerEmail) {
        return TravelDetailResponse.from(travel, includeShareToken, myRating,
                travelService.countTimesDone(travel),
                travelService.getOtherVersionsForViewer(travel, viewerEmail).stream()
                        .map(TravelVersionResponse::from)
                        .toList());
    }

    public PagedResult<TravelSummaryResponse> getPublicTravels(PagingQueryParams queryParams) {
        PagedResult<Travel> page = travelService.getPublicTravels(queryParams);
        List<TravelSummaryResponse> data = page.data.stream()
                .map(TravelSummaryResponse::from)
                .toList();
        return new PagedResult<>(data, page.totalItems);
    }

    public List<TravelSummaryResponse> getUserTravels(Long userId, String viewerEmail) {
        return travelService.getUserTravelsForViewer(userId, viewerEmail).stream()
                .map(TravelSummaryResponse::from)
                .toList();
    }

    private boolean isOwner(Travel travel, String email) {
        return email != null
                && travel.getOwner() != null
                && email.equals(travel.getOwner().getEmail());
    }
}
