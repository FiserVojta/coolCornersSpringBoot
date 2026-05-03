package com.lonework.corners.event.security;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component("eventSecurity")
public class EventSecurity {

    private final EntityManager entityManager;

    public EventSecurity(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean isOwner(Long eventId, String email) {
        if (eventId == null || email == null) {
            return false;
        }
        Long count = entityManager.createQuery(
                        "SELECT COUNT(e) FROM Event e WHERE e.id = :id AND e.createdBy = :email",
                        Long.class)
                .setParameter("id", eventId)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
}
