package com.lonework.corners.comment.controller;

import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.comment.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;


@Configurable
@Service
public class CommentFacade {

    @Autowired
    CommentService commentService;

    public Comment createComment(Comment comment) {
        return commentService.createComment(comment);
    }
}
