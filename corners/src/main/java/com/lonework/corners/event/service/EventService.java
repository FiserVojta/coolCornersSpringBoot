package com.lonework.corners.event.service;

import com.lonework.corners.common.model.EntityStatus;
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
    public List<Event> findEventByParameters(EventSearchParameters eventSearchParameters){
        return entityManager.createQuery("SELECT e from Event e where e.entityStatus = :status", Event.class)
                .setParameter("status", EntityStatus.ACTIVE)
                .getResultList();
    }


}
