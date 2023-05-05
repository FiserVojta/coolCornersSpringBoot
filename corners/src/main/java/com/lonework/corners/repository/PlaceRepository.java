package com.lonework.corners.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lonework.corners.model.Place;

@Repository
public interface PlaceRepository extends CrudRepository<Place, Long> {

    @Query(value = "SELECT * FROM place WHERE city = ?1 ORDER BY random() LIMIT 1", nativeQuery = true)
    Place findRandomByAtributes(String city);

}