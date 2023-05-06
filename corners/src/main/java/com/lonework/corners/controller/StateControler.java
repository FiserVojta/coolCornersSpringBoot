package com.lonework.corners.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.model.State;
import com.lonework.corners.services.StateService;

@RestController
@RequestMapping("/country")
public class StateControler {

    @Autowired
    private StateService stateService;

    @CrossOrigin(origins = "*")
    @GetMapping("")
    public Iterable<State> getAllStates() {

        return this.stateService.getAllStates();
    }

}
