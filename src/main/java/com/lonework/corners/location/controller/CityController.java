package com.lonework.corners.location.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.location.model.City;

@RestController
@RequestMapping("/city")
public class CityController {

    @Autowired
    private CityFacade cityFacade;

    @GetMapping("")
    public Iterable<City> getAllCities() {
        return cityFacade.getAllCities();
    }

    @GetMapping("/country/{countryId}")
    public Iterable<City> getAllCitiesByCountryId(@PathVariable("countryId") Long countryId) {
        return cityFacade.findAllByCountryId(countryId);
    }
}
