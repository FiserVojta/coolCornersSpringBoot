package com.lonework.corners.user.model;

import com.lonework.corners.wander.model.WanderListResponse;

import java.util.List;
import java.util.Set;


public record UserDetailResponse(
        Long id,
        String name,
        String displayName,
        String createdAt,
        String discordId,
        String introduction,
        Integer rating,
        String profilePictureUrl,
        List<WanderListResponse> wandersOrganized,
        List<WanderListResponse> wandersAttended,
        //TO DO fix naming convention
        Set<User> following,
        Set<User> followers
) {
    public UserDetailResponse(User user, List<WanderListResponse> wandersAttended,  List<WanderListResponse> wandersOrganized) {
        this(
                user.getId(),
                user.getName(),
                user.getDisplayName(),
                user.getCreatedAt().toString(),
                user.getDiscordId(),
                user.getIntroduction(),
                null,
                user.getProfilePicture() != null ? user.getProfilePicture().getUrl() : null,
                wandersOrganized,
                wandersAttended,
                user.getFollowers(),
                user.getFollowersOf()
        );
    }
}
