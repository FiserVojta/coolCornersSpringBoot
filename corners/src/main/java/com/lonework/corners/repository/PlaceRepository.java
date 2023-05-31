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
            "LEFT JOIN place_has_tag pt ON pt.place_id = p.id " +
            "LEFT JOIN tag t ON t.id = pt.tag_id " +
            "WHERE (:#{#placeSearchRequest.cityIds} IS NULL OR p.city_id IN :#{#placeSearchRequest.cityIds}) " +
            "AND (:#{#placeSearchRequest.tagIds} IS NULL OR t.id IN (:#{#placeSearchRequest.tagIds})) " +
            "AND (:#{#placeSearchRequest.categoryIds} IS NULL OR p.category_id IN (:#{#placeSearchRequest.categoryIds})) "
            +
            "ORDER BY p.rating DESC", nativeQuery = true)
    Iterable<Place> findRandomByAttributes(@Param("placeSearchRequest") PlaceSearchRequest placeSearchRequest);

}