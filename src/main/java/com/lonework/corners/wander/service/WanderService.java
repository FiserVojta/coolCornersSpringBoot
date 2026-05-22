package com.lonework.corners.wander.service;

import com.lonework.corners.category.api.CategoryOperations;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.files.api.FileOperations;
import com.lonework.corners.tag.api.TagOperations;
import com.lonework.corners.user.api.UserOperations;
import com.lonework.corners.user.model.User;
import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderCreateRequest;
import com.lonework.corners.wander.model.WanderListResponse;
import com.lonework.corners.wander.model.WanderPart;
import com.lonework.corners.wander.model.WanderQueryParam;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Configurable
@Transactional
public class WanderService {

    private final EntityManager entityManager;
    private final CategoryOperations categoryOperations;
    private final FileOperations fileOperations;
    private final TagOperations tagOperations;
    private final UserOperations userOperations;
    private final WanderPartService wanderPartService;

    public WanderService(
            EntityManager entityManager,
            CategoryOperations categoryOperations,
            FileOperations fileOperations,
            TagOperations tagOperations,
            UserOperations userOperations,
            WanderPartService wanderPartService
    ) {
        this.entityManager = entityManager;
        this.categoryOperations = categoryOperations;
        this.fileOperations = fileOperations;
        this.tagOperations = tagOperations;
        this.userOperations = userOperations;
        this.wanderPartService = wanderPartService;
    }

    public Wander createWander(WanderCreateRequest wanderCreateRequest, String createdBy) {
        Wander wander = new Wander();

        wander.setDescription(wanderCreateRequest.description());
        wander.setCapacity(wanderCreateRequest.capacity());
        wander.setStartTime(wanderCreateRequest.startTime());
        wander.setWanderType(wanderCreateRequest.wanderType());

        User creator = userOperations.getRequiredUserByEmail(createdBy);
        wander.setCreatedBy(creator);

        if (wanderCreateRequest.category() != null) {
            wander.setCategory(categoryOperations.getCategoryById(wanderCreateRequest.category()));
        }
        if (wanderCreateRequest.tags() != null && !wanderCreateRequest.tags().isEmpty()) {
            wander.setTags(tagOperations.getTagsById(wanderCreateRequest.tags()));
        }

        if (wanderCreateRequest.wanderers() != null && !wanderCreateRequest.wanderers().isEmpty()) {
            wander.setWanderers(userOperations.getUsersByIds(wanderCreateRequest.wanderers()));
        }

        if (wanderCreateRequest.backgroundImage() != null) {
            wander.setBackgroundImage(fileOperations.getFileMetadata(wanderCreateRequest.backgroundImage().fileId()));
        }

        wander.setWanderParts(new ArrayList<>());

        entityManager.persist(wander);
        entityManager.flush();

        if (wanderCreateRequest.wanderParts() != null && !wanderCreateRequest.wanderParts().isEmpty()) {
            List<WanderPart> wanderParts = wanderCreateRequest.wanderParts().stream()
                    .map(partRequest -> wanderPartService.createWanderPart(partRequest, wander))
                    .toList();
            wander.setWanderParts(wanderParts);
        }

        return wander;
    }

    public PagedResult<WanderListResponse> getWanders(PagingQueryParams queryParams, WanderQueryParam filterParams) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Count query
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Wander> countRoot = countCq.from(Wander.class);
        countCq.select(cb.count(countRoot));
        Predicate[] countPredicates = buildFilterPredicates(cb, countRoot, filterParams);
        if (countPredicates.length > 0) {
            countCq.where(countPredicates);
        }
        long total = entityManager.createQuery(countCq).getSingleResult();

        // Data query
        CriteriaQuery<Wander> dataCq = cb.createQuery(Wander.class);
        Root<Wander> dataRoot = dataCq.from(Wander.class);
        dataCq.select(dataRoot).distinct(true);
        Predicate[] dataPredicates = buildFilterPredicates(cb, dataRoot, filterParams);
        if (dataPredicates.length > 0) {
            dataCq.where(dataPredicates);
        }

        if (filterParams.sortBy() != null) {
            jakarta.persistence.criteria.Expression<?> orderExpression = switch (filterParams.sortBy()) {
                case START_TIME -> dataRoot.get("startTime");
            };
            boolean ascending = filterParams.sortDir() == com.lonework.corners.common.model.QueryOrder.ASC;
            dataCq.orderBy(ascending ? cb.asc(orderExpression) : cb.desc(orderExpression));
        }

        var wanders = entityManager.createQuery(dataCq)
                .setFirstResult(queryParams.page() != null ? queryParams.page() : 0)
                .setMaxResults(queryParams.size() != null ? queryParams.size() : 10)
                .getResultStream()
                .map(WanderListResponse::new)
                .toList();

        return new PagedResult<>(wanders, total);
    }

    private Predicate[] buildFilterPredicates(CriteriaBuilder cb, Root<Wander> root, WanderQueryParam filterParams) {
        List<Predicate> predicates = new ArrayList<>();

        if (filterParams.startsFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), filterParams.startsFrom()));
        }
        if (filterParams.startsUntil() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("startTime"), filterParams.startsUntil()));
        }
        if (filterParams.createdBy() != null) {
            predicates.add(cb.equal(root.get("createdBy").get("id"), filterParams.createdBy()));
        }
        if (filterParams.categories() != null && !filterParams.categories().isEmpty()) {
            CriteriaBuilder.In<Object> inClause = cb.in(root.get("category").get("id"));
            for (Long catId : filterParams.categories()) {
                inClause.value(catId);
            }
            predicates.add(inClause);
        }
        if (filterParams.tags() != null && !filterParams.tags().isEmpty()) {
            Join<Object, Object> tagJoin = root.join("tags");
            predicates.add(tagJoin.get("id").in(filterParams.tags()));
        }

        return predicates.toArray(new Predicate[0]);
    }

    public Wander getWander(Long id) {
        return entityManager.createQuery("SELECT w FROM Wander w where w.id = :id", Wander.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Transactional
    public Wander updateWander(Long wanderId, WanderCreateRequest wanderCreateRequest) {
        Wander wander = entityManager.find(Wander.class, wanderId);
        if (wander == null) {
            throw new EntityNotFoundException("Wander not found with id: " + wanderId);
        }

        wander.setDescription(wanderCreateRequest.description());
        wander.setCapacity(wanderCreateRequest.capacity());
        wander.setStartTime(wanderCreateRequest.startTime());
        wander.setWanderType(wanderCreateRequest.wanderType());

        if (wanderCreateRequest.category() != null) {
            wander.setCategory(categoryOperations.getCategoryById(wanderCreateRequest.category()));
        } else {
            wander.setCategory(null);
        }

        if (wanderCreateRequest.tags() != null && !wanderCreateRequest.tags().isEmpty()) {
            wander.setTags(tagOperations.getTagsById(wanderCreateRequest.tags()));
        } else {
            wander.setTags(new ArrayList<>());
        }

        if (wanderCreateRequest.wanderers() != null && !wanderCreateRequest.wanderers().isEmpty()) {
            wander.setWanderers(userOperations.getUsersByIds(wanderCreateRequest.wanderers()));
        } else {
            wander.setWanderers(new ArrayList<>());
        }

        if (wanderCreateRequest.backgroundImage() != null) {
            wander.setBackgroundImage(fileOperations.getFileMetadata(wanderCreateRequest.backgroundImage().fileId()));
        } else {
            wander.setBackgroundImage(null);
        }

        // Remove old wander parts
        if (wander.getWanderParts() != null && !wander.getWanderParts().isEmpty()) {
            entityManager.createQuery("DELETE FROM WanderPart wp WHERE wp.wander.id = :wanderId")
                    .setParameter("wanderId", wanderId)
                    .executeUpdate();
        }

        entityManager.flush();

        if (wanderCreateRequest.wanderParts() != null && !wanderCreateRequest.wanderParts().isEmpty()) {
            List<WanderPart> newWanderParts = wanderCreateRequest.wanderParts().stream()
                    .map(partRequest -> wanderPartService.createWanderPart(partRequest, wander))
                    .toList();
            wander.setWanderParts(newWanderParts);
        } else {
            wander.setWanderParts(new ArrayList<>());
        }

        return wander;
    }

    /**
     * Add a user to a wander as a wanderer
     */
    @Transactional
    public Wander joinWander(Long wanderId, String userEmail) {
        // Find the wander
        Wander wander = entityManager.find(Wander.class, wanderId);
        if (wander == null) {
            throw new EntityNotFoundException("Wander not found with id: " + wanderId);
        }

        // Find the user
        User user = userOperations.getRequiredUserByEmail(userEmail);

        // Check if user is already a wanderer
        if (wander.getWanderers() != null && wander.getWanderers().contains(user)) {
            throw new IllegalStateException("User is already a wanderer of this wander");
        }

        // Check capacity if set
        if (wander.getCapacity() != null && wander.getWanderers() != null
                && wander.getWanderers().size() >= wander.getCapacity()) {
            throw new IllegalStateException("Wander has reached its capacity");
        }

        // Add user to wanderers
        if (wander.getWanderers() == null) {
            wander.setWanderers(new ArrayList<>());
        }
        wander.getWanderers().add(user);

        // Save and return
        return entityManager.merge(wander);
    }

    /**
     * Remove a user from a wander
     */
    @Transactional
    public Wander leaveWander(Long wanderId, String userEmail) {
        // Find the wander
        Wander wander = entityManager.find(Wander.class, wanderId);
        if (wander == null) {
            throw new EntityNotFoundException("Wander not found with id: " + wanderId);
        }

        // Find the user
        User user = userOperations.getRequiredUserByEmail(userEmail);

        // Check if user is a wanderer
        if (wander.getWanderers() == null || !wander.getWanderers().contains(user)) {
            throw new IllegalStateException("User is not a wanderer of this wander");
        }

        // Remove user from wanderers
        wander.getWanderers().remove(user);

        // Save and return
        return entityManager.merge(wander);
    }

    /**
     * Delete a wander
     */
    @Transactional
    public void deleteWander(Long wanderId) {
        Wander wander = entityManager.find(Wander.class, wanderId);
        if (wander == null) {
            throw new EntityNotFoundException("Wander not found with id: " + wanderId);
        }

        // Remove wander parts first
        if (wander.getWanderParts() != null) {
            for (WanderPart part : wander.getWanderParts()) {
                entityManager.remove(part);
            }
        }

        entityManager.remove(wander);
    }

    public List<WanderListResponse> getWanderListResponse(List<Wander> wanders) {
        List<WanderListResponse> wanderListResponses = new ArrayList<>();
        for (Wander wander : wanders) {
            WanderListResponse wanderListResponse = new WanderListResponse(wander);
            wanderListResponses.add(wanderListResponse);
        }
        return wanderListResponses;
    }

}
