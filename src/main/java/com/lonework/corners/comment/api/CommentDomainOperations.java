package com.lonework.corners.comment.api;

import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.comment.services.CommentService;
import org.springframework.stereotype.Component;

@Component
public class CommentDomainOperations implements CommentOperations {

    private final CommentService commentService;

    public CommentDomainOperations(CommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    public Comment createComment(Comment comment) {
        return commentService.createComment(comment);
    }
}
