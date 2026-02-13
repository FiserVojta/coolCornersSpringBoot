package com.lonework.corners.place.controller;

import com.lonework.corners.place.model.DTO.GooglePlaceCreateRequest;
import com.lonework.corners.place.model.GooglePlace;
import com.lonework.corners.place.model.Place;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;


@Service
public class PlaceFacade {

    @Autowired
    EntityManager entityManager;

    public List<GooglePlace> getCreateGooglePlaces(List<GooglePlaceCreateRequest> googlePlaceIds) {
        List<GooglePlace> list = new ArrayList<>();
        for (GooglePlaceCreateRequest googlePlaceCreateRequest : googlePlaceIds) {
            list.add(createGooglePlace(googlePlaceCreateRequest));
        }
        return list;
    }

    public List<Place> findPlacesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return entityManager
                .createQuery("SELECT p FROM Place p WHERE p.id IN :ids", Place.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Transactional
    public GooglePlace createGooglePlace(GooglePlaceCreateRequest googlePlaceCreateRequest) {
        var entity = entityManager
                .createQuery(
                        "SELECT g FROM GooglePlace g WHERE g.id = :id",
                        GooglePlace.class
                )
                .setParameter("id", googlePlaceCreateRequest.placeId())
                .getResultStream()
                .findFirst()
                .orElse(new GooglePlace());
        entity.setId(googlePlaceCreateRequest.placeId());
        entity.setName(googlePlaceCreateRequest.name());
        entity.setGeometry(googlePlaceCreateRequest.geometry());
        return entityManager.merge(entity);
    }
}
