package com.lonework.corners.user.model;

public record UserListResponse(
        Long id,
        String name,
        String displayName,
        Double rating,
        String createdAt,
        Long tripsCompleted,
        Long cotravelsOrganized,
        Long cotravelsAttended
) {
    public UserListResponse(User user, Long tripsCompleted, Long cotravelsOrganized, Long cotravelsAttended) {
        this(
                user.getId(),
                user.getName(),
                user.getDisplayName(),
                user.getRating(),
                user.getCreatedAt() != null ? user.getCreatedAt().toString() : null,
                tripsCompleted,
                cotravelsOrganized,
                cotravelsAttended
        );
    }

    public UserListResponse(User user) {
        this(user, null, null, null);
    }
}
