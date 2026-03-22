package com.lonework.corners.location.controller;

import com.lonework.corners.location.model.Country;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CountryFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private CountryFacade countryFacade;

    @Test
    void getAllCountriesReturnsPersistedCountries() {
        createCountry("Czechia", "CZ");
        createCountry("Slovakia", "SK");
        flushAndClear();

        List<Country> countries = ((List<Country>) countryFacade.getAllCountries()).stream()
                .sorted(Comparator.comparing(Country::getCode))
                .toList();

        assertTrue(countries.stream().anyMatch(country -> "CZ".equals(country.getCode())));
        assertTrue(countries.stream().anyMatch(country -> "SK".equals(country.getCode())));
    }
}
