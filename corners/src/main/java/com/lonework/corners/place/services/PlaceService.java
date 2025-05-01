package com.lonework.corners.place.services;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.comment.model.CommentCreateRequest;
import com.lonework.corners.place.model.DTO.PlaceCreateRequest;
import com.lonework.corners.place.model.DTO.PlaceRateRequest;
import com.lonework.corners.place.model.PlaceRating;
import com.lonework.corners.place.model.DTO.PlaceSearchRequest;
import com.lonework.corners.place.model.PlaceDetailResponse;
import com.lonework.corners.common.model.FeatureTypeClass;
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

    public PlaceDetailResponse getPlaceResponse(Long id){
        var place = getPlaceById(id);
        return
                new PlaceDetailResponse(
                        place,
                        getPlaceFeature(place)
                );
    }

    public SimpleFeature getPlaceFeature(Place place){
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureTypeClass.placeFeatureType());
        featureBuilder.set("name", place.getName());
        featureBuilder.set("id", place.getId().toString());
        featureBuilder.set("geometry", place.getGeometry());
        return featureBuilder.buildFeature(place.getId().toString());
    }

    public Iterable<Place> findPlacesByParameters(PlaceSearchRequest placeSearchRequest) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(Place.class);
        Root<Place> root = query.from(Place.class);

        Predicate categoryPredicate = criteriaBuilder.conjunction();
        if (placeSearchRequest.getCategory() != null && !placeSearchRequest.getCategory().isEmpty()) {
            CriteriaBuilder.In<Long> categoryInClause = criteriaBuilder.in(root.join("category").get("id")); // Assuming "category" is a field name
            for (Long categoryId : placeSearchRequest.getCategory()) {
                categoryInClause = categoryInClause.value(categoryId);
            }
            categoryPredicate = criteriaBuilder.and(categoryPredicate, categoryInClause);
        }
        if(placeSearchRequest.getTags() != null && !placeSearchRequest.getTags().isEmpty()){
            CriteriaBuilder.In<Long> tagsInClause = criteriaBuilder.in(root.join("tags").get("id")); // Assuming "tags" is a field name
            for (Long tagId : placeSearchRequest.getTags()) {
                tagsInClause = tagsInClause.value(tagId);
            }
            categoryPredicate = criteriaBuilder.and(categoryPredicate, tagsInClause);
        }

        query.select(root).where(categoryPredicate);

        return entityManager.createQuery(query).getResultList();
    }

    @Transactional
    public Place createPlace(PlaceCreateRequest placeRequest) {
        placeRequest.getGeometry().setSRID(4326);
        Place place = new Place(placeRequest);
        place.setCategory(entityManager.find(Category.class, placeRequest.getCategoryId()));
        place.setTags(entityManager.createQuery("SELECT t FROM Tag t WHERE t.id IN :ids", Tag.class)
                .setParameter("ids", placeRequest.getTags())
                .getResultList());
        entityManager.persist(place);
        return null;
    }

    @Transactional
    public Comment createComment(CommentCreateRequest request, long placeId) {
        Comment comment = new Comment();
        comment.setAuthor(request.getAuthor());
        comment.setName(request.getName());
        comment.setTitle(request.getTitle());
        comment.setName(request.getName());
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
