package com.lonework.corners.comment.model;

public record CommentCreateRequest(
        String name,
        String value,
        String author
) {
}
