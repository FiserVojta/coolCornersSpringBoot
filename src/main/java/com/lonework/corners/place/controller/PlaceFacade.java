package com.lonework.corners.place.controller;

import com.lonework.corners.place.model.DTO.GooglePlaceCreateRequest;
import com.lonework.corners.place.model.DTO.PlaceCreateRequest;
import com.lonework.corners.place.model.DTO.PlaceRateRequest;
import com.lonework.corners.place.model.DTO.PlaceSearchRequest;
import com.lonework.corners.place.model.GooglePlace;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.comment.model.CommentCreateRequest;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.place.model.PlaceDetailResponse;
import com.lonework.corners.place.services.PlaceService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.geotools.api.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PlaceFacade {

    @Autowired
    EntityManager entityManager;

    @Autowired
    PlaceService placeService;

    public Place createPlace(PlaceCreateRequest placeRequest, String email) {
        return placeService.createPlace(placeRequest, email);
    }

    public Place updatePlace(PlaceCreateRequest placeRequest, String email, Long placeId) {
        return placeService.updatePlace(placeRequest, email, placeId);
    }

    public Comment commentPlace(CommentCreateRequest commentCreateRequest, long placeId, String email) {
        return placeService.commentPlace(commentCreateRequest, placeId, email);
    }

    public Double ratePlace(PlaceRateRequest placeRateRequest, Long placeId) {
        return placeService.ratePlace(placeRateRequest, placeId);
    }

    public PlaceDetailResponse getPlaceResponse(Long id) {
        return placeService.getPlaceResponse(id);
    }

    public PagedResult<Place> findPlacesByParameters(PlaceSearchRequest placeSearchRequest, PagingQueryParams pagingQueryParams) {
        return placeService.findPlacesByParameters(placeSearchRequest, pagingQueryParams);
    }

    public SimpleFeature getPlaceFeature(Place place) {
        return placeService.getPlaceFeature(place);
    }

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

    public Place getPlaceById(Long id) {
        return placeService.getPlaceById(id);
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
