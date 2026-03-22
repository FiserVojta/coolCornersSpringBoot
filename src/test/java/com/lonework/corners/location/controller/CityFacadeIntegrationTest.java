package com.lonework.corners.location.controller;

import com.lonework.corners.location.model.City;
import com.lonework.corners.location.model.Country;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CityFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private CityFacade cityFacade;

    @Test
    void getAllCitiesReturnsPersistedCities() {
        Country country = createCountry("Czechia", "CZ");
        createCity("Prague", country);
        createCity("Brno", country);
        flushAndClear();

        List<City> cities = cityFacade.getAllCities().stream()
                .sorted(Comparator.comparing(City::getName))
                .toList();

        assertTrue(cities.stream().anyMatch(city -> "Brno".equals(city.getName())));
        assertTrue(cities.stream().anyMatch(city -> "Prague".equals(city.getName())));
    }

    @Test
    void findAllByCountryIdFiltersCitiesByCountry() {
        Country czechia = createCountry("Czechia", "CZ");
        Country slovakia = createCountry("Slovakia", "SK");
        createCity("Prague", czechia);
        createCity("Bratislava", slovakia);
        flushAndClear();

        List<City> cities = cityFacade.findAllByCountryId(czechia.getId());

        assertEquals(1, cities.size());
        assertEquals("Prague", cities.getFirst().getName());
    }
}
