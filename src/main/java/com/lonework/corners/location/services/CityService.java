package com.lonework.corners.location.services;

import com.lonework.corners.location.model.City;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Configurable
public class CityService {

    @Autowired
    EntityManager entityManager;

    public List<City> getAllCities() {
        return entityManager.createQuery("SELECT c FROM City c", City.class).getResultList();
    }

    public List<City> findAllByCountryId(Long countryId) {
        return entityManager.createQuery("SELECT c FROM City c WHERE c.country.id = :countryId ORDER BY name", City.class)
                .setParameter("countryId", countryId)
                .getResultList();
    }
}
