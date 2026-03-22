package com.lonework.corners.location.controller;

import com.lonework.corners.location.model.City;
import com.lonework.corners.location.services.CityService;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CityFacade {

    @Inject
    CityService cityService;

    public List<City> getAllCities() {
        return cityService.getAllCities();
    }

    public List<City> findAllByCountryId(Long countryId) {
        return cityService.findAllByCountryId(countryId);
    }
}
