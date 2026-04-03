package com.lonework.corners.trip.controller;

import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.place.model.DTO.PlaceListRequest;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.trip.model.DTO.TripCommentRequest;
import com.lonework.corners.trip.model.DTO.TripCreateRequest;
import com.lonework.corners.trip.model.DTO.TripDetailResponse;
import com.lonework.corners.trip.model.DTO.TripRateRequest;
import com.lonework.corners.trip.model.DTO.TripUpdateRequest;
import com.lonework.corners.trip.model.TripSearchRequest;
import com.lonework.corners.trip.services.TripService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class TripFacade {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TripService tripService;

    public Trip createTrip(TripCreateRequest tripRequest, String email) {
        return tripService.createTrip(tripRequest, email);
    }

    public Optional<Trip> addPlacesToTrip(PlaceListRequest placeListRequest, Long id) {
        return tripService.addPlacesToTrip(placeListRequest, id);
    }

    public Double rateTrip(TripRateRequest tripRateRequest, Long tripId) {
        return tripService.rateTrip(tripRateRequest, tripId);
    }

    public void commentTrip(TripCommentRequest tripCommentRequest, Long tripId, String email) {
        tripService.commentTrip(tripCommentRequest, tripId, email);
    }

    public void updateTrip(TripUpdateRequest tripUpdateRequest, Long tripId, String email) {
        tripService.updateTrip(tripUpdateRequest, tripId, email);
    }

    public TripDetailResponse findTripById(long id) {
        return tripService.findTripById(id);
    }

    public TripDetailResponse markTripDone(Long tripId, String email) {
        return tripService.markTripDone(tripId, email);
    }

    public PagedResult<Trip> findTripByParameters(TripSearchRequest tripSearchRequest, PagingQueryParams queryParams) {
        return tripService.findTripByParameters(tripSearchRequest, queryParams);
    }

    public List<Trip> findTripsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return entityManager
                .createQuery("SELECT t FROM Trip t WHERE t.id IN :ids", Trip.class)
                .setParameter("ids", ids)
                .getResultList();
    }
}
