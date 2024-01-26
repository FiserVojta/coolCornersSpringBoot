package com.lonework.corners.services;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.lonework.corners.model.Place;
import com.lonework.corners.model.Trip;
import com.lonework.corners.model.TripHasPlace;
import com.lonework.corners.model.request.PlaceListRequest;
import com.lonework.corners.model.request.TripCreateRequest;
import com.lonework.corners.model.request.TripSearchRequest;
import com.lonework.corners.repository.PlaceRepository;
import com.lonework.corners.repository.TripHasPlaceRepository;
import com.lonework.corners.repository.TripRepository;

import jakarta.transaction.Transactional;

@Service
@Configurable
@Transactional
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripHasPlaceRepository tripHasPlaceRepository;

    @Autowired
    private PlaceRepository placeRepository;

    public Trip crateTrip(TripCreateRequest tripCreateRequest) {

        Iterable<Place> places = this.placeRepository.findAllById(tripCreateRequest.getPlaceIdList());
        Set<Place> placesSet = new HashSet<>();

        for (Place place : places) {
            placesSet.add(place);
        }
        Trip trip = tripCreateRequest.getTrip();

        trip.setPlaces(placesSet);
        System.out.println(trip.getPlaces().size());

        return this.tripRepository.save(trip);
    }

    public Optional<Trip> findTripById(long id) {

        return this.tripRepository.findById(id);
    }

    public Optional<Trip> addPlacesToTrip(PlaceListRequest placeListRequest, Long tripId) {
        // Iterable<TripHasPlace> tripsHasPlaces =
        System.out.println(placeListRequest.getPlaceIds());
        for (Long placeId : placeListRequest.getPlaceIds()) {

            tripHasPlaceRepository.save(new TripHasPlace(placeId, tripId));
        }

        return tripRepository.findById(tripId);
    }

    public Iterable<Trip> findTripByparameters(TripSearchRequest tripSearchRequest) {

        Iterable<Trip> trips = tripRepository.findAllByParametrs(
                tripSearchRequest.getTags());
        return trips;
    }

}
