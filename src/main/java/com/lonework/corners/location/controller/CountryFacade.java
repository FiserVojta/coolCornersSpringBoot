package com.lonework.corners.location.controller;

import com.lonework.corners.location.model.Country;
import com.lonework.corners.location.services.CountryService;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;


@Service
public class CountryFacade {

    @Inject
    CountryService countryService;

    public Iterable<Country> getAllCountries() {
        return countryService.getAllCountries();
    }
}
