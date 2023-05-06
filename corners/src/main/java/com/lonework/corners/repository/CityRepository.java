package com.lonework.corners.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.lonework.corners.model.City;

public interface CityRepository extends CrudRepository<City, Long> {

    @Query(value = "SELECT * FROM city WHERE state_id = ?1 ORDER BY name", nativeQuery = true)
    Iterable<City> findAllByStateId(Long stateId);

}
