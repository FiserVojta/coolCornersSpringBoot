package com.lonework.corners.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.lonework.corners.model.City;
import com.lonework.corners.repository.CityRepository;

@Service
@Configurable
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    public Iterable<City> getAllCities() {
        return cityRepository.findAll();
    }

    public Iterable<City> findAllByStateId(Long stateId) {
        return cityRepository.findAllByStateId(stateId);
    }
}
