package com.lonework.corners.event.service;

import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.event.model.DTO.EventCreateRequest;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.event.model.EventSearchParameters;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

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
        var data = entityManager.createQuery("SELECT e from Event e where e.entityStatus = :status", Event.class)
                .setParameter("status", EntityStatus.ACTIVE)
                .setFirstResult(queryParams.page() != null ? queryParams.page() : 0)
                .setMaxResults(queryParams.size() != null ? queryParams.size() : 10)
                .getResultList();
        long total = entityManager.createQuery("SELECT COUNT(e) FROM Event e where e.entityStatus = :status", Long.class)
                .setParameter("status", EntityStatus.ACTIVE)
                .getSingleResult();
        return new PagedResult<>(data, total);
    }


}
