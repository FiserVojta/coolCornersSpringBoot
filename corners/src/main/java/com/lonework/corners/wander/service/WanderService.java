package com.lonework.corners.wander.service;

import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderCreateRequest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
@Transactional
public class WanderService {

    @Inject
    EntityManager entityManager;

    public Wander createWander(WanderCreateRequest wanderCreateRequest, String createdBy) {
        Wander wander = new Wander(wanderCreateRequest, createdBy);

        return entityManager.merge(wander);
    }

    public List<Wander> getAllWanders() {
        return entityManager.createQuery("SELECT w FROM Wander w", Wander.class).getResultList();
    }

    public Wander getWander(Long id) {
        return entityManager.createQuery("SELECT w FROM Wander w where w.id = :id", Wander.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}
