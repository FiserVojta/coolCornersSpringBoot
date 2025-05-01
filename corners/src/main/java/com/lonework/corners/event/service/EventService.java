package com.lonework.corners.event.service;

import com.lonework.corners.event.model.DTO.EventCreateRequest;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.event.model.EventSearchParameters;
import jakarta.persistence.EntityManager;
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
   public Event createEvent(EventCreateRequest eventCreateRequest){
         Event event = new Event(eventCreateRequest);
         return entityManager.merge(event);
   }

    @Transactional
    public Event getEvent(Long id){
        return entityManager.find(Event.class, id);
    }

    @Transactional
    public List<Event> findEventByParameters(EventSearchParameters eventSearchParameters){
        return entityManager.createQuery("SELECT e from Event e", Event.class).getResultList();

    }


}
