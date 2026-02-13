package com.lonework.corners.trip.controller;

import com.lonework.corners.trip.model.Trip;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TripFacade {

    @Autowired
    EntityManager entityManager;

    public List<Trip> findTripsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return entityManager
                .createQuery("SELECT t FROM Trip t WHERE t.id IN :ids", Trip.class)
                .setParameter("ids", ids)
                .getResultList();
    }
}
