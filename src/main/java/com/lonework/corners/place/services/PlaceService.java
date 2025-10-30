package com.lonework.corners.place.services;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.comment.model.CommentCreateRequest;
import com.lonework.corners.common.model.FeatureTypeClass;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.place.model.DTO.PlaceCreateRequest;
import com.lonework.corners.place.model.DTO.PlaceRateRequest;
import com.lonework.corners.place.model.DTO.PlaceSearchRequest;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.place.model.PlaceDetailResponse;
import com.lonework.corners.place.model.PlaceRating;
import com.lonework.corners.tag.model.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Configurable
public class PlaceService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    FeatureTypeClass featureTypeClass;

    public Place getPlaceById(Long id) {
        return entityManager.find(Place.class, id);
    }

    public PlaceDetailResponse getPlaceResponse(Long id) {
        var place = getPlaceById(id);
        return new PlaceDetailResponse(
                place,
                getPlaceFeature(place)
        );
    }

    public SimpleFeature getPlaceFeature(Place place) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureTypeClass.placeFeatureType());
        featureBuilder.set("name", place.getName());
        featureBuilder.set("id", place.getId().toString());
        featureBuilder.set("geometry", place.getGeometry());
        return featureBuilder.buildFeature(place.getId().toString());
    }

    public PagedResult<Place> findPlacesByParameters(PlaceSearchRequest placeSearchRequest, PagingQueryParams pagingQueryParams) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // --- Main query ---
        var query = criteriaBuilder.createQuery(Place.class);
        Root<Place> root = query.from(Place.class);

        Predicate predicate = buildPredicate(criteriaBuilder, root, placeSearchRequest);

        query.select(root).where(predicate);

        var data = entityManager.createQuery(query)
                .setFirstResult(pagingQueryParams.page() != null ? pagingQueryParams.page() : 0)
                .setMaxResults(pagingQueryParams.size() != null ? pagingQueryParams.size() : 10)
                .getResultList();

        // --- Count query ---
        var countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Place> countRoot = countQuery.from(Place.class);
        Predicate countPredicate = buildPredicate(criteriaBuilder, countRoot, placeSearchRequest);
        countQuery.select(criteriaBuilder.count(countRoot)).where(countPredicate);
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PagedResult<>(data, total);
    }

    private Predicate buildPredicate(CriteriaBuilder cb, Root<Place> root, PlaceSearchRequest request) {
        Predicate predicate = cb.conjunction();

        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            CriteriaBuilder.In<Long> categoryInClause = cb.in(root.join("category").get("id"));
            for (Long categoryId : request.getCategory()) {
                categoryInClause.value(categoryId);
            }
            predicate = cb.and(predicate, categoryInClause);
        }

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            CriteriaBuilder.In<Long> tagsInClause = cb.in(root.join("tags").get("id"));
            for (Long tagId : request.getTags()) {
                tagsInClause.value(tagId);
            }
            predicate = cb.and(predicate, tagsInClause);
        }

        return predicate;
    }

    @Transactional
    public Place createPlace(PlaceCreateRequest placeRequest, String email) {
        placeRequest.getGeometry().setSRID(4326);
        Place place = new Place(placeRequest, email);
        place.setCategory(entityManager.find(Category.class, placeRequest.getCategoryId()));
        place.setTags(entityManager.createQuery("SELECT t FROM Tag t WHERE t.id IN :ids", Tag.class)
                .setParameter("ids", placeRequest.getTags())
                .getResultList());
        entityManager.persist(place);
        return null;
    }

    @Transactional
    public Place updatePlace(PlaceCreateRequest placeRequest, String email, Long placeId) {
        placeRequest.getGeometry().setSRID(4326);
        Place place = new Place(placeRequest, email);
        place.setId(placeId);
        place.setCategory(entityManager.find(Category.class, placeRequest.getCategoryId()));
        place.setTags(entityManager.createQuery("SELECT t FROM Tag t WHERE t.id IN :ids", Tag.class)
                .setParameter("ids", placeRequest.getTags())
                .getResultList());
        entityManager.merge(place);
        return null;
    }


    @Transactional
    public Comment commentPlace(CommentCreateRequest request, long placeId, String email) {
        Comment comment = new Comment();
        comment.setAuthor(email);
        comment.setTitle(request.getTitle());
        comment.setName(email);
        comment.setValue(request.getValue());
        comment.setPlace(entityManager.find(Place.class, placeId));
        comment.setCreated(LocalDateTime.now());
        entityManager.persist(comment);
        return comment;
    }

    @Transactional
    public Double ratePlace(PlaceRateRequest placeRateRequest, Long placeId) {
        var place = entityManager.find(Place.class, placeId);
        if (place == null) {
            throw new EntityNotFoundException("Place not found");
        }
        entityManager.merge(new PlaceRating(placeRateRequest, placeId));
        //place.setRating(countTripRating(tripId));
        //entityManager.merge(trip);

        return null;
    }
}
