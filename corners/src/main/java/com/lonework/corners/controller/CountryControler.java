package com.lonework.corners.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.model.Country;
import com.lonework.corners.services.CountryService;

@RestController
@RequestMapping("/country")
public class CountryControler {

    @Autowired
    private CountryService stateService;

    @CrossOrigin(origins = "*")
    @GetMapping("")
    public Iterable<Country> getAllStates() {

        return this.stateService.getAllCountries();
    }

}
