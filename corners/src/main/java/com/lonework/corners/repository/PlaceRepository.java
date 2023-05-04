package com.lonework.corners.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lonework.corners.model.Place;

@Repository
public interface PlaceRepository extends CrudRepository<Place, Long> {
}