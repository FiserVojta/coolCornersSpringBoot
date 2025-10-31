package com.lonework.corners.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record UserFollowRequest(
        @JsonProperty List<Long> userIds) {
}
