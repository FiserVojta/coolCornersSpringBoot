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
import com.lonework.corners.common.model.PagingQueryParams;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


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
    void findPlacesByParametersRequiresAllSelectedTagsAndReturnsEachPlaceOnce() {
        Category category = createCategory("Place Category", CategoryType.PLACE);
        Tag quiet = createTag("quiet", "integration@example.com");
        Tag view = createTag("view", "integration@example.com");
        createPlace("Both Tags", category, List.of(quiet, view), "integration@example.com");
        createPlace("Quiet Only", category, List.of(quiet), "integration@example.com");
        createPlace("View Only", category, List.of(view), "integration@example.com");
        flushAndClear();

        var both = placeFacade.findPlacesByParameters(
                new PlaceSearchRequest(null, List.of(quiet.getId(), view.getId()), null, null), createPagingQueryParams());
        var quietOnly = placeFacade.findPlacesByParameters(
                new PlaceSearchRequest(null, List.of(quiet.getId(), quiet.getId()), null, null), createPagingQueryParams());

        assertEquals(List.of("Both Tags"), both.data.stream().map(Place::getName).toList());
        assertEquals(1L, both.totalItems);
        assertEquals(List.of("Both Tags", "Quiet Only"), quietOnly.data.stream().map(Place::getName).sorted().toList());
        assertEquals(2L, quietOnly.totalItems);
    }

    @Test
    void findPlacesByParametersReturnsDisjointPagesCoveringAllMatches() {
        Category category = createCategory("Paged Category", CategoryType.PLACE);
        for (int i = 1; i <= 5; i++) {
            createPlace("Paged Place " + i, category, List.of(), "integration@example.com");
        }
        flushAndClear();
        PlaceSearchRequest request = new PlaceSearchRequest(null, null, List.of(category.getId()), null);

        List<String> seen = new ArrayList<>();
        for (int page = 0; page < 3; page++) {
            var result = placeFacade.findPlacesByParameters(request, new PagingQueryParams(page, 2, null, null));
            assertEquals(5L, result.totalItems);
            assertEquals(page < 2 ? 2 : 1, result.data.size());
            result.data.forEach(place -> seen.add(place.getName()));
        }

        assertEquals(List.of("Paged Place 1", "Paged Place 2", "Paged Place 3", "Paged Place 4", "Paged Place 5"),
                seen.stream().sorted().toList());
    }

    @Test
    void createAndUpdatePlaceEnforceTagAssignmentRules() {
        Category category = createCategory("Place Category", CategoryType.PLACE);
        List<Long> elevenIds = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            elevenIds.add(createTag("place cap " + (char) ('a' + i), "integration@example.com").getId());
        }
        Tag hidden = createTag("place hidden", "integration@example.com");
        hidden.setStatus(com.lonework.corners.tag.model.TagStatus.HIDDEN);
        Place withHidden = createPlace("Has Hidden", category, List.of(hidden), "integration@example.com");
        flushAndClear();

        assertThrows(IllegalArgumentException.class, () -> placeFacade.createPlace(
                createPlaceCreateRequest(category.getId(), elevenIds, "Too Many"), "integration@example.com"));
        assertThrows(IllegalArgumentException.class, () -> placeFacade.createPlace(
                createPlaceCreateRequest(category.getId(), List.of(hidden.getId()), "New Hidden"), "integration@example.com"));

        placeFacade.updatePlace(createPlaceCreateRequest(category.getId(), List.of(hidden.getId(), elevenIds.getFirst()),
                "Has Hidden"), "integration@example.com", withHidden.getId());
        flushAndClear();

        assertEquals(2, entityManager.find(Place.class, withHidden.getId()).getTags().size());
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
