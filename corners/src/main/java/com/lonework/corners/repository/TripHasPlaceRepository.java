package com.lonework.corners.repository;

import org.springframework.data.repository.CrudRepository;

import com.lonework.corners.model.TripHasPlace;

public interface TripHasPlaceRepository extends CrudRepository<TripHasPlace, Long> {
}