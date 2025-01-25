package com.lonework.corners.services;

import com.lonework.corners.model.Category;
import com.lonework.corners.model.Trip;
import com.lonework.corners.model.request.PlaceListRequest;
import com.lonework.corners.model.request.TripCreateRequest;
import com.lonework.corners.model.request.TripSearchRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Configurable
@Transactional
public class TripService {

    @Autowired
    EntityManager entityManager;

    public Trip crateTrip(TripCreateRequest tripCreateRequest) {

        //        if (tripCreateRequest.getPlaceIdList() != null && !tripCreateRequest.getPlaceIdList().isEmpty()) {
        //            //Iterable<Place> places = this.placeRepository.findAllById(tripCreateRequest.getPlaceIdList());
        //            Set<Place> placesSet = new HashSet<>();
        //                        for (Place place : places) {
        //                placesSet.add(place);
        //            }
        //        }
        Trip trip = tripCreateRequest.getTrip();
        trip.setCategory(entityManager.find(Category.class, tripCreateRequest.getCategoryId()));
        return entityManager.merge(trip);
    }

    public Trip findTripById(long id) {

        return entityManager.find(Trip.class, id);
    }

    public Optional<Trip> addPlacesToTrip(PlaceListRequest placeListRequest, Long tripId) {

        System.out.println(placeListRequest.getPlaceIds());
        for (Long placeId : placeListRequest.getPlaceIds()) {


        }
        return Optional.ofNullable(entityManager.find(Trip.class, tripId));
    }

    public Iterable<Trip> findTripByparameters(TripSearchRequest tripSearchRequest) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Trip> cq = cb.createQuery(Trip.class);
        Root<Trip> tripRoot = cq.from(Trip.class);
        if (tripSearchRequest.getCategories() != null && !tripSearchRequest.getCategories().isEmpty()) {
            var categoryPredicate = tripRoot.get("category").get("id").in(tripSearchRequest.getCategories());
            cq.where(categoryPredicate);
        }


        return entityManager.createQuery(cq).getResultList();
    }

}
