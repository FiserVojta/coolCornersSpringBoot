package com.lonework.corners.services;

import com.lonework.corners.model.Category;
import com.lonework.corners.model.Comment;
import com.lonework.corners.model.Place;
import com.lonework.corners.model.request.CommentCreateRequest;
import com.lonework.corners.model.request.PlaceCreateRequest;
import com.lonework.corners.model.request.PlaceSearchRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@Configurable
public class PlaceService {

    @Autowired
    EntityManager entityManager;

    public Place getPlaceById(Long id) {
        Optional<Place> placeOptional = Optional.ofNullable(entityManager.find(Place.class, id));
        return placeOptional.orElse(null);
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
        query.select(root).where(categoryPredicate);


        //        Predicate tagPredicate = criteriaBuilder.conjunction();
        //        if(placeSearchRequest.getTagIds() != null && !placeSearchRequest.getTagIds().isEmpty()) {
        //            CriteriaBuilder.In<Long> tagInClause = criteriaBuilder.in(root.get("tags").get("id"));
        //            for (Long value : placeSearchRequest.getTagIds()) {
        //                tagInClause.value(value);
        //            }
        //            tagPredicate = criteriaBuilder.and(tagPredicate, tagInClause);
        //        }
        //        query.select(root).where(tagPredicate);
        return entityManager.createQuery(query).getResultList();
    }

    @Transactional
    public Place createPlace(PlaceCreateRequest placeRequest) {
        placeRequest.getGeometry().setSRID(4326);
        Place place = new Place(placeRequest);
        place.setCategory(entityManager.find(Category.class, placeRequest.getCategoryId()));
        System.out.println(place.getGeometry().toText());
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
}
