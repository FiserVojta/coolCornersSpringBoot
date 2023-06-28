package com.lonework.corners.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.repository.TripRepository;
import com.lonework.corners.services.TripService;

@RestController
@RequestMapping("/trip")
public class TripController {

    @Autowired
    private TripService tripService;

}
