package com.lonework.corners.wander.controller;

import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderDetailResponse;
import com.lonework.corners.wander.model.WanderListResponse;
import com.lonework.corners.wander.model.WanderQueryParam;
import com.lonework.corners.wander.service.WanderService;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;


@ApplicationScope
@Service
public class WanderFacade {

    @Inject
    WanderService wanderService;

    public WanderDetailResponse getWander(Long id) {
        return new WanderDetailResponse(wanderService.getWander(id));
    }

    public PagedResult<WanderListResponse> getWanderListResponse(PagingQueryParams queryParams, WanderQueryParam filterParams) {
        return wanderService.getWanders(queryParams, filterParams);
    }

    public List<WanderListResponse> getWanderListResponse(List<Wander> wanders) {
        return wanderService.getWanderListResponse(wanders);
    }
}
