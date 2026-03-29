package com.lonework.corners.user.service;


import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.user.model.User;
import com.lonework.corners.user.model.UserDetailResponse;
import com.lonework.corners.user.model.UserListResponse;
import com.lonework.corners.user.model.UserSearchParameters;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.wander.controller.WanderFacade;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;


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

    public PagedResult<UserListResponse> getUserList(UserSearchParameters userSearchParameters, PagingQueryParams queryParams) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        int page = queryParams.page() != null ? queryParams.page() : 0;
        int size = queryParams.size() != null ? queryParams.size() : 10;

        CriteriaQuery<User> dataQuery = cb.createQuery(User.class);
        Root<User> dataRoot = dataQuery.from(User.class);
        dataQuery.select(dataRoot);
        dataQuery.where(buildPredicates(userSearchParameters, cb, dataRoot));
        dataQuery.orderBy(cb.asc(dataRoot.get("id")));

        var users = entityManager.createQuery(dataQuery)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultStream()
                .map(UserListResponse::new)
                .toList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countRoot = countQuery.from(User.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(buildPredicates(userSearchParameters, cb, countRoot));

        long total = entityManager.createQuery(countQuery).getSingleResult();
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

    public List<Trip> getUserTrips(String email) {
        User user = getUser(email);
        if (user == null) {
            return List.of();
        }
        return user.getCompletedTrips();
    }

    public List<User> getUsersByIds(List<Long> ids) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.id IN :ids", User.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    private Predicate[] buildPredicates(UserSearchParameters userSearchParameters, CriteriaBuilder cb, Root<User> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (userSearchParameters != null
                && userSearchParameters.search() != null
                && !userSearchParameters.search().isBlank()) {
            String searchPattern = "%" + userSearchParameters.search().trim().toLowerCase(Locale.ROOT) + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("displayName")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern)
            ));
        }

        return predicates.toArray(new Predicate[0]);
    }
}
