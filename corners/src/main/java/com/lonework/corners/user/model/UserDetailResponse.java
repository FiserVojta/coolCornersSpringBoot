package com.lonework.corners.user.model;

public record UserDetailResponse(
        Long id,
        String email,
        String name,
        String displayName,
        String createdAt,
        String discordId,
        Integer rating
) {
    public UserDetailResponse(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getDisplayName(),
                user.getCreatedAt().toString(),
                null,
                null
        );
    }
}
