package com.lonework.corners.location.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.location.model.City;
import com.lonework.corners.location.services.CityService;

@RestController
@RequestMapping("/city")
public class CityControler {

    @Autowired
    private CityService cityService;

    @CrossOrigin(origins = "*")
    @GetMapping("")
    public Iterable<City> getAllCities() {
        return this.cityService.getAllCities();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/country/{countryId}")
    public Iterable<City> getAllCitiesByCountryId(@PathVariable("countryId") Long countryId) {
        System.out.println("here" + countryId);
        return this.cityService.findAllByCountryId(countryId);
    }
}
