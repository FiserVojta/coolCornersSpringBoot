package com.lonework.corners.user.model;

import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderListResponse;

import java.util.List;


public record UserDetailResponse(
        Long id,
        String email,
        String name,
        String displayName,
        String createdAt,
        String discordId,
        Integer rating,
        List<WanderListResponse> wandersOrganized,
        List<WanderListResponse> wandersAttended
) {
    public UserDetailResponse(User user, List<WanderListResponse> wandersAttended,  List<WanderListResponse> wandersOrganized) {
        this(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getDisplayName(),
                user.getCreatedAt().toString(),
                null,
                null,
                wandersOrganized,
                wandersAttended
        );
    }
}
