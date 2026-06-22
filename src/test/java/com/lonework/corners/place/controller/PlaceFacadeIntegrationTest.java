package com.lonework.corners.place.controller;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategoryType;
import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.comment.model.CommentCreateRequest;
import com.lonework.corners.place.model.DTO.GooglePlaceCreateRequest;
import com.lonework.corners.place.model.DTO.PlaceCreateRequest;
import com.lonework.corners.place.model.DTO.PlaceSearchRequest;
import com.lonework.corners.place.model.GooglePlace;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import com.lonework.corners.tag.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class PlaceFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private PlaceFacade placeFacade;

    @Test
    void createPlacePersistsPlace() {
        Category category = createCategory("Place Category", CategoryType.PLACE);
        Tag tag = createTag("coffee", "integration@example.com");
        flushAndClear();

        placeFacade.createPlace(createPlaceCreateRequest(category.getId(), List.of(tag.getId()), "Place One"), "integration@example.com");
        flushAndClear();

        Place place = entityManager.createQuery("SELECT p FROM Place p WHERE p.name = :name", Place.class)
                .setParameter("name", "Place One")
                .getSingleResult();

        assertEquals("integration@example.com", place.getCreatedBy());
        assertEquals(category.getId(), place.getCategory().getId());
        assertEquals(1, place.getTags().size());
    }

    @Test
    void updatePlacePersistsChangedValues() {
        Category category = createCategory("Original Category", CategoryType.PLACE);
        Tag originalTag = createTag("coffee", "integration@example.com");
        placeFacade.createPlace(createPlaceCreateRequest(category.getId(), List.of(originalTag.getId()), "Original Place"), "integration@example.com");
        flushAndClear();

        Place persistedPlace = entityManager.createQuery("SELECT p FROM Place p WHERE p.name = :name", Place.class)
                .setParameter("name", "Original Place")
                .getSingleResult();
        Tag updatedTag = createTag("view", "integration@example.com");
        flushAndClear();

        placeFacade.updatePlace(createPlaceCreateRequest(category.getId(), List.of(updatedTag.getId()), "Updated Place"), "integration@example.com", persistedPlace.getId());
        flushAndClear();

        Place updatedPlace = entityManager.find(Place.class, persistedPlace.getId());

        assertEquals("Updated Place", updatedPlace.getName());
        assertEquals(1, updatedPlace.getTags().size());
        assertEquals("view", updatedPlace.getTags().getFirst().getName());
    }

    @Test
    void commentPlacePersistsComment() {
        Category category = createCategory("Place Category", CategoryType.PLACE);
        Tag tag = createTag("coffee", "integration@example.com");
        placeFacade.createPlace(createPlaceCreateRequest(category.getId(), List.of(tag.getId()), "Commented Place"), "integration@example.com");
        flushAndClear();

        Place place = entityManager.createQuery("SELECT p FROM Place p WHERE p.name = :name", Place.class)
                .setParameter("name", "Commented Place")
                .getSingleResult();
        CommentCreateRequest request = new CommentCreateRequest(null, "Strong coffee", null);

        Comment comment = placeFacade.commentPlace(request, place.getId(), "integration@example.com");
        flushAndClear();

        Comment persistedComment = entityManager.find(Comment.class, comment.getId());

        assertNotNull(persistedComment);
        assertEquals("Strong coffee", persistedComment.getValue());
        assertEquals(place.getId(), persistedComment.getPlace().getId());
    }

    @Test
    void findPlacesByParametersFiltersPersistedPlaces() {
        Category category = createCategory("Place Category", CategoryType.PLACE);
        Tag selectedTag = createTag("selected", "integration@example.com");
        Tag otherTag = createTag("other", "integration@example.com");
        createPlace("Selected Place", category, List.of(selectedTag), "integration@example.com");
        createPlace("Other Place", category, List.of(otherTag), "integration@example.com");
        flushAndClear();

        PlaceSearchRequest request = new PlaceSearchRequest(null, List.of(selectedTag.getId()), List.of(category.getId()), null);

        List<Place> places = placeFacade.findPlacesByParameters(request, createPagingQueryParams()).data.stream()
                .sorted(Comparator.comparing(Place::getName))
                .toList();

        assertEquals(1, places.size());
        assertEquals("Selected Place", places.getFirst().getName());
    }

    @Test
    void createGooglePlacePersistsAndFindsEntity() {
        GooglePlace googlePlace = placeFacade.createGooglePlace(
                new GooglePlaceCreateRequest("google-1", "Google Place", createPoint(14.4, 50.1), null)
        );
        flushAndClear();

        GooglePlace persistedGooglePlace = entityManager.find(GooglePlace.class, googlePlace.getId());

        assertNotNull(persistedGooglePlace);
        assertEquals("Google Place", persistedGooglePlace.getName());
    }

    private PlaceCreateRequest createPlaceCreateRequest(Long categoryId, List<Long> tagIds, String name) {
        return new PlaceCreateRequest(
                name,
                name + " description",
                0.0,
                "+420123123123",
                25.0,
                "09:00-18:00",
                name + "-image",
                null,
                categoryId,
                createPoint(14.4, 50.1),
                tagIds
        );
    }
}
