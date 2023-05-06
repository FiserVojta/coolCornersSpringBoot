package com.lonework.corners.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.lonework.corners.model.State;
import com.lonework.corners.repository.StateRepository;

@Service
@Configurable
public class StateService {

    @Autowired
    private StateRepository stateRepository;

    public Iterable<State> getAllStates() {
        return stateRepository.findAll();
    }
}
