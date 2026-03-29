package com.lonework.corners.event.service;

import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.event.model.DTO.EventCreateRequest;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.event.model.EventSearchParameters;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Configurable
public class EventService {

    @Autowired
    EntityManager entityManager;

    @Transactional
   public Event createEvent(EventCreateRequest eventCreateRequest, String createdBy){
        var category = entityManager.find(com.lonework.corners.category.model.Category.class, eventCreateRequest.categoryId());
         Event event = new Event(eventCreateRequest, category, createdBy);
         return entityManager.merge(event);
   }

    @Transactional
    public Event updatedEvent(EventCreateRequest eventCreateRequest, Long id){
        var category = entityManager.find(com.lonework.corners.category.model.Category.class, eventCreateRequest.categoryId());
        Event event = new Event(eventCreateRequest, category, eventCreateRequest.createdBy());
        event.setId(id);
        return entityManager.merge(event);
    }

    @Transactional
    public Event getEvent(Long id){
        return entityManager.find(Event.class, id);
    }


    @Transactional
    public void deleteEvent(Long id){
        var event = entityManager.find(Event.class, id);
        if(event == null){
            throw new EntityNotFoundException();
        }
        event.setEntityStatus(EntityStatus.DELETED);
        entityManager.merge(event);
    }

    @Transactional
    public PagedResult<Event> findEventByParameters(EventSearchParameters eventSearchParameters, PagingQueryParams queryParams){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        int page = queryParams.page() != null ? queryParams.page() : 0;
        int size = queryParams.size() != null ? queryParams.size() : 10;

        CriteriaQuery<Event> dataQuery = cb.createQuery(Event.class);
        Root<Event> dataRoot = dataQuery.from(Event.class);
        dataQuery.where(buildPredicates(eventSearchParameters, cb, dataRoot));

        var data = entityManager.createQuery(dataQuery)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Event> countRoot = countQuery.from(Event.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(buildPredicates(eventSearchParameters, cb, countRoot));

        long total = entityManager.createQuery(countQuery).getSingleResult();
        return new PagedResult<>(data, total);
    }

    private jakarta.persistence.criteria.Predicate[] buildPredicates(
            EventSearchParameters eventSearchParameters,
            CriteriaBuilder cb,
            Root<Event> root
    ) {
        List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("entityStatus"), EntityStatus.ACTIVE));

        if (eventSearchParameters.getCreatedBy() != null && !eventSearchParameters.getCreatedBy().isBlank()) {
            predicates.add(cb.equal(root.get("createdBy"), eventSearchParameters.getCreatedBy()));
        }
        if (eventSearchParameters.getCategories() != null && !eventSearchParameters.getCategories().isEmpty()) {
            predicates.add(root.get("category").get("id").in(eventSearchParameters.getCategories()));
        }

        return predicates.toArray(new jakarta.persistence.criteria.Predicate[0]);
    }


}
