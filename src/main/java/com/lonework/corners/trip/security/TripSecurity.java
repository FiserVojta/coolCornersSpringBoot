package com.lonework.corners.trip.security;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component("tripSecurity")
public class TripSecurity {

    private final EntityManager entityManager;

    public TripSecurity(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean isOwner(Long tripId, String email) {
        if (tripId == null || email == null) {
            return false;
        }
        Long count = entityManager.createQuery(
                        "SELECT COUNT(t) FROM Trip t WHERE t.id = :id AND t.createdBy = :email",
                        Long.class)
                .setParameter("id", tripId)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
}
