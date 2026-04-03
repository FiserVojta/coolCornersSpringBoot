package com.lonework.corners.wander.api;

import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderListResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WanderDomainOperations implements WanderOperations {

    @Override
    public List<WanderListResponse> getWanderListResponse(List<Wander> wanders) {
        return wanders.stream().map(WanderListResponse::new).toList();
    }
}
