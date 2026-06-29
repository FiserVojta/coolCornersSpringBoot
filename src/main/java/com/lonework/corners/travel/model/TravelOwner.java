package com.lonework.corners.travel.model;

import com.lonework.corners.user.model.User;


public record TravelOwner(
        Long id,
        String name,
        String displayName,
        String profilePictureUrl
) {
    public static TravelOwner from(User user) {
        if (user == null) {
            return null;
        }
        return new TravelOwner(
                user.getId(),
                user.getName(),
                user.getDisplayName(),
                user.getProfilePicture() != null ? user.getProfilePicture().getUrl() : null
        );
    }
}
