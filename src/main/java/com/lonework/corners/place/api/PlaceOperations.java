package com.lonework.corners.place.api;

import com.lonework.corners.place.model.DTO.GooglePlaceCreateRequest;
import com.lonework.corners.place.model.GooglePlace;
import com.lonework.corners.place.model.Place;
import org.geotools.api.feature.simple.SimpleFeature;

import java.util.List;

public interface PlaceOperations {

    Place getPlaceById(Long id);

    List<Place> findPlacesByIds(List<Long> ids);

    List<GooglePlace> getOrCreateGooglePlaces(List<GooglePlaceCreateRequest> requests);

    SimpleFeature getPlaceFeature(Place place);
}
