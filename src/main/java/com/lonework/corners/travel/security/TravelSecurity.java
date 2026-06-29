package com.lonework.corners.travel.security;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component("travelSecurity")
public class TravelSecurity {

    private final EntityManager entityManager;

    public TravelSecurity(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean isOwner(Long travelId, String email) {
        if (travelId == null || email == null) {
            return false;
        }
        Long count = entityManager.createQuery(
                        "SELECT COUNT(t) FROM Travel t WHERE t.id = :id AND t.owner.email = :email",
                        Long.class)
                .setParameter("id", travelId)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
}
