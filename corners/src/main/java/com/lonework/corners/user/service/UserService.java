package com.lonework.corners.user.service;


import com.lonework.corners.user.model.User;
import com.lonework.corners.user.model.UserDetailResponse;
import com.lonework.corners.user.model.UserListResponse;
import com.lonework.corners.wander.controller.WanderFacade;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;


@Service
@Configurable
@Transactional
public class UserService {

    @Inject
    EntityManager entityManager;

    @Inject
    WanderFacade wanderFacade;

    public User getUser(String email) {
        return entityManager.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    public UserDetailResponse getUserDetail(String email) {
        var user = entityManager.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();

        return new UserDetailResponse(user, wanderFacade.getWanderListResponse(user.getWanders()));
    }

    public List<UserListResponse> getUserList() {
        return entityManager.createQuery("select u from User u", User.class)
                .getResultStream()
                .map(UserListResponse::new)
                .toList();
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
