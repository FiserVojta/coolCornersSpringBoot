package com.lonework.corners.comment.services;


import com.lonework.corners.comment.model.Comment;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;


@Service
@Configurable
public class CommentService {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public Comment createComment(Comment comment) {
        return entityManager.merge(comment);
    }
}
