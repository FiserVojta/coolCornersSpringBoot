package com.lonework.corners.repository;

import org.springframework.data.repository.CrudRepository;

import com.lonework.corners.model.Comment;
import com.lonework.corners.model.CustomLocation;

public interface LocationRepository extends CrudRepository<CustomLocation, Long> {

}
