package com.lonework.corners.place.api;

import com.lonework.corners.place.model.DTO.GooglePlaceCreateRequest;
import com.lonework.corners.place.model.GooglePlace;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.place.services.PlaceService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.geotools.api.feature.simple.SimpleFeature;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlaceDomainOperations implements PlaceOperations {

    private final EntityManager entityManager;
    private final PlaceService placeService;

    public PlaceDomainOperations(EntityManager entityManager, PlaceService placeService) {
        this.entityManager = entityManager;
        this.placeService = placeService;
    }

    @Override
    public Place getPlaceById(Long id) {
        return placeService.getPlaceById(id);
    }

    @Override
    public List<Place> findPlacesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return entityManager
                .createQuery("SELECT p FROM Place p WHERE p.id IN :ids", Place.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    @Transactional
    public List<GooglePlace> getOrCreateGooglePlaces(List<GooglePlaceCreateRequest> requests) {
        List<GooglePlace> googlePlaces = new ArrayList<>();
        for (GooglePlaceCreateRequest request : requests) {
            googlePlaces.add(getOrCreateGooglePlace(request));
        }
        return googlePlaces;
    }

    @Override
    public SimpleFeature getPlaceFeature(Place place) {
        return placeService.getPlaceFeature(place);
    }

    private GooglePlace getOrCreateGooglePlace(GooglePlaceCreateRequest request) {
        GooglePlace entity = entityManager
                .createQuery("SELECT g FROM GooglePlace g WHERE g.id = :id", GooglePlace.class)
                .setParameter("id", request.placeId())
                .getResultStream()
                .findFirst()
                .orElse(new GooglePlace());
        entity.setId(request.placeId());
        entity.setName(request.name());
        entity.setGeometry(request.geometry());
        return entityManager.merge(entity);
    }
}
