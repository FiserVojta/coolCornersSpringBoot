package com.lonework.corners.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.lonework.corners.repository.TripRepository;

@Service
@Configurable
public class TripService {

    @Autowired
    private TripRepository tripRepository;
}
