package com.lonework.corners.user.model;

import com.lonework.corners.wander.model.Wander;

import java.util.List;
import java.util.Set;


public record UserDetailResponse(
        Long id,
        String email,
        String name,
        String displayName,
        String createdAt,
        String discordId,
        Integer rating,
        List<Wander> wanderList
) {
    public UserDetailResponse(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getDisplayName(),
                user.getCreatedAt().toString(),
                null,
                null,
                user.getWanders()
        );
    }
}
