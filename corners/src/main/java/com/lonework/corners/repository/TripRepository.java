package com.lonework.corners.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.lonework.corners.model.Tag;
import com.lonework.corners.model.Trip;

public interface TripRepository extends CrudRepository<Trip, Long> {

    @Query("SELECT t FROM Tag t JOIN t.places p WHERE p.id IN :placeIds")
    // @Query("SELECT c FROM Tag c WHERE c.place_id IN :placeIds")
    List<Tag> findAllByPlaceIds(@Param("placeIds") List<Long> placeIds);

    @Query("SELECT t FROM Trip t JOIN t.tags ta WHERE (ta.id IN :tags)")
    // @Query("SELECT c FROM Tag c WHERE c.place_id IN :placeIds")
    Iterable<Trip> findAllByParametrs(@Param("tags") List<Long> tags);
}
