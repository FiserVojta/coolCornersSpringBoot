package com.lonework.corners.repository;

import org.springframework.data.repository.CrudRepository;

import com.lonework.corners.model.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {

}
