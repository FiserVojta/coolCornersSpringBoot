package com.lonework.corners.wander.service;

import com.lonework.corners.place.model.Place;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderPart;
import com.lonework.corners.wander.model.WanderPartCreateRequest;
import com.lonework.corners.wander.model.WanderPartUpdateRequest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Configurable
@Transactional
public class WanderPartService {

    @Inject
    EntityManager entityManager;

    public WanderPart createWanderPart(WanderPartCreateRequest request, Wander wander) {
        WanderPart wanderPart = new WanderPart();
        wanderPart.setOrder(request.order());
        wanderPart.setWander(wander);
        wanderPart.setPlaces(resolvePlaces(request.places()));
        wanderPart.setTrips(resolveTrips(request.trips()));

        entityManager.persist(wanderPart);
        return wanderPart;
    }

    public WanderPart updateWanderPart(Long wanderPartId, WanderPartUpdateRequest request) {
        WanderPart wanderPart = entityManager.find(WanderPart.class, wanderPartId);
        if (wanderPart == null) {
            throw new EntityNotFoundException("WanderPart not found with id: " + wanderPartId);
        }

        wanderPart.setOrder(request.order());
        wanderPart.setPlaces(resolvePlaces(request.places()));
        wanderPart.setTrips(resolveTrips(request.trips()));

        return entityManager.merge(wanderPart);
    }

    private List<Place> resolvePlaces(List<Long> placeIds) {
        if (placeIds == null || placeIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Place> places = entityManager.createQuery("SELECT p FROM Place p WHERE p.id IN :ids", Place.class)
                .setParameter("ids", placeIds)
                .getResultList();
        if (places.size() != placeIds.size()) {
            throw new EntityNotFoundException("Some places were not found");
        }
        return places;
    }

    private List<Trip> resolveTrips(List<Long> tripIds) {
        if (tripIds == null || tripIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Trip> trips = entityManager.createQuery("SELECT t FROM Trip t WHERE t.id IN :ids", Trip.class)
                .setParameter("ids", tripIds)
                .getResultList();
        if (trips.size() != tripIds.size()) {
            throw new EntityNotFoundException("Some trips were not found");
        }
        return trips;
    }
}
