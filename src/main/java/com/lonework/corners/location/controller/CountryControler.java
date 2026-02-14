package com.lonework.corners.location.controller;

import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.location.model.Country;
import com.lonework.corners.location.services.CountryService;

@RestController
@RequestMapping("/public/country")
public class CountryControler {

    @Autowired
    private CountryService stateService;

    @GetMapping("")
    @PermitAll
    public Iterable<Country> getAllCountries() {
        return this.stateService.getAllCountries();
    }

}
