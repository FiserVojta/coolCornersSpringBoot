package com.lonework.corners.wander.service;

import com.lonework.corners.place.api.PlaceOperations;
import com.lonework.corners.trip.api.TripOperations;
import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderPart;
import com.lonework.corners.wander.model.WanderPartCreateRequest;
import com.lonework.corners.wander.model.WanderPartUpdateRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;


@Service
@Configurable
@Transactional
public class WanderPartService {

    private final EntityManager entityManager;
    private final PlaceOperations placeOperations;
    private final TripOperations tripOperations;

    public WanderPartService(EntityManager entityManager, PlaceOperations placeOperations, TripOperations tripOperations) {
        this.entityManager = entityManager;
        this.placeOperations = placeOperations;
        this.tripOperations = tripOperations;
    }

    public WanderPart createWanderPart(WanderPartCreateRequest request, Wander wander) {
        WanderPart wanderPart = new WanderPart();
        wanderPart.setOrder(request.order());
        wanderPart.setWander(wander);
        wanderPart.setPlaces(placeOperations.findPlacesByIds(request.places()));
        wanderPart.setTrips(tripOperations.findTripsByIds(request.trips()));
        wanderPart.setName(request.name());
        entityManager.persist(wanderPart);
        return wanderPart;
    }

    public WanderPart updateWanderPart(Long wanderPartId, WanderPartUpdateRequest request) {
        WanderPart wanderPart = entityManager.find(WanderPart.class, wanderPartId);
        if (wanderPart == null) {
            throw new EntityNotFoundException("WanderPart not found with id: " + wanderPartId);
        }

        wanderPart.setOrder(request.order());
        wanderPart.setPlaces(placeOperations.findPlacesByIds(request.places()));
        wanderPart.setTrips(tripOperations.findTripsByIds(request.trips()));
        wanderPart.setName(request.name());
        return entityManager.merge(wanderPart);
    }
}
