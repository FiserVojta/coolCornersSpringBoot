package com.lonework.corners.user.service;


import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
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

        return new UserDetailResponse(user,
                wanderFacade.getWanderListResponse(user.getWanders()),
                wanderFacade.getWanderListResponse(user.getWandersOrganized()));
    }

    public PagedResult<UserListResponse> getUserList(PagingQueryParams queryParams) {

        long total = entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                .getSingleResult();

        var users = entityManager.createQuery("SELECT u FROM User u", User.class)
                .setFirstResult(queryParams.page() != null ? queryParams.page() : 0)
                .setMaxResults(queryParams.size() != null ? queryParams.size() : 10)
                .getResultStream()
                .map(UserListResponse::new)
                .toList();

        return new PagedResult<>(users, total);
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

    public User getUser(Long id){
        return entityManager.find(User.class, id);
    }

    public List<User> getUsersByIds(List<Long> ids) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.id IN :ids", User.class)
                .setParameter("ids", ids)
                .getResultList();
    }
}
