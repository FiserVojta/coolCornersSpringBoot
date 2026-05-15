package com.lonework.corners.user.model;

public record UserUpdateRequest(
        String name,
        String displayName,
        String discordId
) {
}
