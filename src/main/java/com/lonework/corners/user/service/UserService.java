package com.lonework.corners.user.service;


import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.user.model.User;
import com.lonework.corners.user.model.UserDetailResponse;
import com.lonework.corners.user.model.UserListResponse;
import com.lonework.corners.user.model.UserRateRequest;
import com.lonework.corners.user.model.UserRating;
import com.lonework.corners.user.model.UserSearchParameters;
import com.lonework.corners.user.model.UserUpdateRequest;
import com.lonework.corners.wander.api.WanderOperations;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
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

    private final EntityManager entityManager;
    private final WanderOperations wanderOperations;

    public UserService(EntityManager entityManager, WanderOperations wanderOperations) {
        this.entityManager = entityManager;
        this.wanderOperations = wanderOperations;
    }

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
                wanderOperations.getWanderListResponse(user.getWanders()),
                wanderOperations.getWanderListResponse(user.getWandersOrganized()));
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

    public User updateUser(String email, UserUpdateRequest request) {
        User user = getUser(email);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        if (request.name() != null) {
            String name = request.name().isBlank() ? null : request.name().trim();
            user.setName(name);
        }
        if (request.displayName() != null) {
            String displayName = request.displayName().isBlank() ? null : request.displayName().trim();
            user.setDisplayName(displayName);
        }
        if (request.discordId() != null) {
            String discordId = request.discordId().isBlank() ? null : request.discordId().trim();
            user.setDiscordId(discordId);
        }
        return entityManager.merge(user);
    }

    public List<Trip> getUserTrips(String email) {
        User user = getUser(email);
        if (user == null) {
            return List.of();
        }
        return user.getCompletedTrips();
    }

    public List<Place> getUserPlaces(String email) {
        if (email == null || email.isBlank()) {
            return List.of();
        }
        return entityManager
                .createQuery("select p from Place p where p.createdBy = :email order by p.id desc", Place.class)
                .setParameter("email", email)
                .getResultList();
    }

    @Transactional
    public Double rateUser(UserRateRequest request, Long userId) {
        var user = entityManager.find(User.class, userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        entityManager.merge(new UserRating(request, userId));
        user.setRating(countUserRating(userId));
        entityManager.merge(user);
        return user.getRating();
    }

    public List<User> getUsersByIds(List<Long> ids) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.id IN :ids", User.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    private Double countUserRating(Long userId) {
        return entityManager
                .createQuery("SELECT AVG(ur.rating) FROM UserRating ur WHERE ur.userId = :userId", Double.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    private Predicate[] buildPredicates(UserSearchParameters userSearchParameters, CriteriaBuilder cb, Root<User> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (userSearchParameters == null) {
            return predicates.toArray(new Predicate[0]);
        }

        if (userSearchParameters.search() != null && !userSearchParameters.search().isBlank()) {
            String searchPattern = "%" + userSearchParameters.search().trim().toLowerCase(Locale.ROOT) + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("displayName")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern)
            ));
        }

        if (userSearchParameters.minRating() != null && userSearchParameters.minRating() > 0) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), userSearchParameters.minRating()));
        }

        return predicates.toArray(new Predicate[0]);
    }
}
