package com.lonework.corners.wander.security;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component("wanderSecurity")
public class WanderSecurity {

    private final EntityManager entityManager;

    public WanderSecurity(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean isOwner(Long wanderId, String email) {
        if (wanderId == null || email == null) {
            return false;
        }
        Long count = entityManager.createQuery(
                        "SELECT COUNT(w) FROM Wander w WHERE w.id = :id AND w.createdBy.email = :email",
                        Long.class)
                .setParameter("id", wanderId)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
}
