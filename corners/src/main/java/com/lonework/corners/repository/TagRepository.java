package com.lonework.corners.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.lonework.corners.model.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t JOIN t.places p WHERE p.id IN :placeIds")
    // @Query("SELECT c FROM Tag c WHERE c.place_id IN :placeIds")
    List<Tag> findAllByPlaceIds(@Param("placeIds") List<Long> placeIds);
}