package com.lonework.corners.user.model;

public record UserListResponse(
        Long id,
        String email,
        String name,
        String displayName,
        Integer rating
) {
    public UserListResponse(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getDisplayName(),
                null
        );
    }
}
