package com.lonework.corners.travel.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.travel.model.Travel;
import com.lonework.corners.travel.model.TravelCreateRequest;
import com.lonework.corners.travel.model.TravelDetailResponse;
import com.lonework.corners.travel.model.TravelSummaryResponse;
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

    public TravelDetailResponse getTravel(Long travelId, String viewerEmail) {
        Travel travel = travelService.getTravelForViewer(travelId, viewerEmail);
        return TravelDetailResponse.from(travel, isOwner(travel, viewerEmail));
    }

    public TravelDetailResponse getPublicTravel(Long travelId) {
        return TravelDetailResponse.from(travelService.getPublicTravel(travelId), false);
    }

    public TravelDetailResponse getSharedTravel(String shareToken) {
        return TravelDetailResponse.from(travelService.getByShareToken(shareToken), false);
    }

    public PagedResult<TravelSummaryResponse> getPublicTravels(PagingQueryParams queryParams) {
        PagedResult<Travel> page = travelService.getPublicTravels(queryParams);
        List<TravelSummaryResponse> data = page.data.stream()
                .map(TravelSummaryResponse::from)
                .toList();
        return new PagedResult<>(data, page.totalItems);
    }

    public List<TravelSummaryResponse> getPublicUserTravels(Long userId) {
        return travelService.getPublicUserTravels(userId).stream()
                .map(TravelSummaryResponse::from)
                .toList();
    }

    private boolean isOwner(Travel travel, String email) {
        return email != null
                && travel.getOwner() != null
                && email.equals(travel.getOwner().getEmail());
    }
}
