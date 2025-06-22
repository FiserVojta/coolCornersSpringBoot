package com.lonework.corners.user.model;

public record UserListlResponse(
        Long id,
        String email,
        String name,
        String displayName,
        Integer rating
) {
    public UserListlResponse(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getDisplayName(),
                null
        );
    }
}
