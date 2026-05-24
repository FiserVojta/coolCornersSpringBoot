package com.lonework.corners.user.model;

import java.time.ZonedDateTime;
import java.util.Set;

public record MeResponse(
        Long id,
        String keycloakId,
        String email,
        String name,
        String displayName,
        String discordId,
        String introduction,
        Double rating,
        ZonedDateTime createdAt,
        String profilePictureUrl,
        Set<User> followers
) {
    public MeResponse(User user) {
        this(
                user.getId(),
                user.getKeycloakId(),
                user.getEmail(),
                user.getName(),
                user.getDisplayName(),
                user.getDiscordId(),
                user.getIntroduction(),
                user.getRating(),
                user.getCreatedAt(),
                user.getProfilePicture() != null ? user.getProfilePicture().getUrl() : null,
                user.getFollowers()
        );
    }
}
