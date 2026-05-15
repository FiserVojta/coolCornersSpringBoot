package com.lonework.corners.user.model;

public record UserListResponse(
        Long id,
        String name,
        String displayName,
        Double rating
) {
    public UserListResponse(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getDisplayName(),
                user.getRating()
        );
    }
}
