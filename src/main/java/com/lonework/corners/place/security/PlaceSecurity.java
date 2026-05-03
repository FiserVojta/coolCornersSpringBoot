package com.lonework.corners.place.security;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component("placeSecurity")
public class PlaceSecurity {

    private final EntityManager entityManager;

    public PlaceSecurity(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean isOwner(Long placeId, String email) {
        if (placeId == null || email == null) {
            return false;
        }
        Long count = entityManager.createQuery(
                        "SELECT COUNT(p) FROM Place p WHERE p.id = :id AND p.createdBy = :email",
                        Long.class)
                .setParameter("id", placeId)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
}
