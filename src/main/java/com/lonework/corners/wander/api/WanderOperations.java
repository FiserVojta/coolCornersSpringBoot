package com.lonework.corners.wander.api;

import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderListResponse;

import java.util.List;

public interface WanderOperations {

    List<WanderListResponse> getWanderListResponse(List<Wander> wanders);
}
