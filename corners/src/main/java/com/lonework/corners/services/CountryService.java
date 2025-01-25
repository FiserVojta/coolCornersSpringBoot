package com.lonework.corners.services;

import com.lonework.corners.model.Country;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;


@Service
@Configurable
public class CountryService {

    @Autowired
    private EntityManager entityManager;

    public Iterable<Country> getAllCountries() {
        return entityManager.createQuery("select c from Country c", Country.class).getResultList();
    }
}
