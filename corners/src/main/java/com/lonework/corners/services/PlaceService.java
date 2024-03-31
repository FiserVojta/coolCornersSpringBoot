package com.lonework.corners.services;

import java.util.Optional;

import com.lonework.corners.model.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.lonework.corners.repository.CategoryRepository;
import com.lonework.corners.repository.CommentRepository;
import com.lonework.corners.repository.PlaceRepository;
import com.lonework.corners.repository.CountryRepository;
import com.lonework.corners.model.Category;
import com.lonework.corners.model.City;
import com.lonework.corners.model.Place;
import com.lonework.corners.model.request.CommentCreateRequest;
import com.lonework.corners.model.request.PlaceCreateRequest;
import com.lonework.corners.model.request.PlaceSearchRequest;

@Service
@Configurable
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CountryRepository stateRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    EntityManager entityManager;

    public Place getPlaceById(Long id) {
        Optional<Place> placeOptional = this.placeRepository.findById(id);
        if (placeOptional.isPresent()) {
            return placeOptional.get();
        } else {
            return null;
        }

    }

    public Iterable<Place> findPlacesByParametrs(PlaceSearchRequest placeSearchRequest) {
        CriteriaBuilder  criteriaBuilder = entityManager.getCriteriaBuilder();
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
        System.out.println("" + query.toString());
        return entityManager.createQuery(query).getResultList();
    }

    public Place createPlace(PlaceCreateRequest placeRequest) {
        Place place = new Place(placeRequest);
        Optional<Category> cat = categoryRepository.findById(placeRequest.getCategoryId());
        place.setCategory(cat.get());
        return this.placeRepository.save(place);

    }

    public Comment createComment(CommentCreateRequest request, long placeId) {
        return null;
    }
}
