package com.lonework.corners.comment.controller;

import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class CommentFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private CommentFacade commentFacade;

    @Test
    void createCommentPersistsComment() {
        Comment comment = new Comment();
        comment.setName("Integration Tester");
        comment.setValue("Facade comment");
        comment.setAuthor("integration@example.com");
        comment.setCreated(LocalDateTime.parse("2026-03-22T10:15:00"));

        Comment createdComment = commentFacade.createComment(comment);
        flushAndClear();

        Comment persistedComment = entityManager.find(Comment.class, createdComment.getId());

        assertNotNull(persistedComment);
        assertEquals("Facade comment", persistedComment.getValue());
        assertEquals("integration@example.com", persistedComment.getAuthor());
    }
}
