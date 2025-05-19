package com.lonework.corners.user.service;


import com.lonework.corners.user.model.User;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;


@Service
@Configurable
@Transactional
public class UserService {

    @Inject
    EntityManager entityManager;

    public User getUser(String email){
        return entityManager.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    public void ensureUserExists(String keycloakId, String email, String name) {
        entityManager.createQuery("select u from User u where u.keycloakId = :keycloakId", User.class)
                .setParameter("keycloakId", keycloakId)
                .getResultList()
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    User user = new User();
                    user.setKeycloakId(keycloakId);
                    user.setEmail(email);
                    user.setName(name);
                    user.setDisplayName(name);
                    user.setCreatedAt(ZonedDateTime.now());
                    entityManager.persist(user);
                    return user;
                });
    }
}
