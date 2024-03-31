package com.lonework.corners.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.lonework.corners.model.Country;
import com.lonework.corners.repository.CountryRepository;

@Service
@Configurable
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    public Iterable<Country> getAllCountries() {
        return countryRepository.findAll();
    }
}
