package com.lonework.corners.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lonework.corners.model.Place;
import com.lonework.corners.model.request.PlaceSearchRequest;

@Repository
public interface PlaceRepository extends CrudRepository<Place, Long> {

    @Query(value = "SELECT DISTINCT p.* FROM place p " +
            "JOIN place_has_tag pt ON pt.place_id = p.id " +
            "JOIN tag t ON t.id = pt.tag_id " +
            "WHERE (:#{#placeSearchRequest.cityId} IS NULL OR p.city_id = :#{#placeSearchRequest.cityId}) " +
            "AND (:#{#placeSearchRequest.tagIds} IS NULL OR t.id IN (:#{#placeSearchRequest.tagIds})) " +
            "ORDER BY p.rating", nativeQuery = true)
    Iterable<Place> findRandomByAtributes(@Param("placeSearchRequest") PlaceSearchRequest placeSearchRequest);
}