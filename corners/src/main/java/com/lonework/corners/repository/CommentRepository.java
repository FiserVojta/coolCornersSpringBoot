package com.lonework.corners.repository;

import com.lonework.corners.model.Comment;
import org.springframework.data.repository.CrudRepository;


public interface CommentRepository extends CrudRepository<Comment, Long> {

}
