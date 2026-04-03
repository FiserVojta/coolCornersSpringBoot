package com.lonework.corners.trip.api;

import com.lonework.corners.trip.model.Trip;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TripDomainOperations implements TripOperations {

    private final EntityManager entityManager;

    public TripDomainOperations(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
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
