package com.lonework.corners.trip.controller;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategoryType;
import com.lonework.corners.comment.model.Comment;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.trip.model.DTO.TripCommentRequest;
import com.lonework.corners.trip.model.DTO.TripCreateRequest;
import com.lonework.corners.trip.model.DTO.TripRateRequest;
import com.lonework.corners.trip.model.DTO.TripUpdateRequest;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.trip.model.TripRating;
import com.lonework.corners.trip.model.TripSearchRequest;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class TripFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private TripFacade tripFacade;

    @Test
    void createTripPersistsTripWithRelations() {
        Category category = createCategory("Trip Category", CategoryType.TRIP);
        Tag tag = createTag("roadtrip", "integration@example.com");
        Category placeCategory = createCategory("Place Category", CategoryType.PLACE);
        Place place = createPlace("Trip Place", placeCategory, List.of(), "integration@example.com");
        flushAndClear();

        Trip trip = tripFacade.createTrip(createTripCreateRequest(category.getId(), List.of(tag.getId()), List.of(place.getId()), "Trip One"), "integration@example.com");
        flushAndClear();

        Trip persistedTrip = entityManager.find(Trip.class, trip.getId());

        assertNotNull(persistedTrip);
        assertEquals("Trip One", persistedTrip.getName());
        assertEquals(category.getId(), persistedTrip.getCategory().getId());
        assertEquals(1, persistedTrip.getTags().size());
        assertEquals(1, persistedTrip.getPlaces().size());
    }

    @Test
    void updateTripPersistsChangedValues() {
        Category category = createCategory("Trip Category", CategoryType.TRIP);
        Tag originalTag = createTag("roadtrip", "integration@example.com");
        Tag updatedTag = createTag("citybreak", "integration@example.com");
        Trip trip = createTrip("Original Trip", category, List.of(originalTag), List.of(), "integration@example.com");
        flushAndClear();

        tripFacade.updateTrip(
                new TripUpdateRequest(List.of(), category.getId(), "Updated description", List.of(updatedTag.getId()), List.of(), null, null),
                trip.getId(),
                "integration@example.com"
        );
        flushAndClear();

        Trip updatedTrip = entityManager.find(Trip.class, trip.getId());

        assertEquals("Updated description", updatedTrip.getDescription());
        assertEquals(1, updatedTrip.getTags().size());
        assertEquals("citybreak", updatedTrip.getTags().getFirst().getName());
    }

    @Test
    void commentTripAndRateTripPersistRelatedData() {
        Category category = createCategory("Trip Category", CategoryType.TRIP);
        Trip trip = createTrip("Rated Trip", category, List.of(), List.of(), "integration@example.com");
        flushAndClear();

        tripFacade.commentTrip(new TripCommentRequest("Great trip", "integration@example.com"), trip.getId(), "integration@example.com");
        Double rating = tripFacade.rateTrip(new TripRateRequest(4, "integration@example.com"), trip.getId());
        flushAndClear();

        List<Comment> comments = entityManager.createQuery("SELECT c FROM Comment c WHERE c.trip.id = :tripId", Comment.class)
                .setParameter("tripId", trip.getId())
                .getResultList();
        List<TripRating> ratings = entityManager.createQuery("SELECT tr FROM TripRating tr WHERE tr.tripId = :tripId", TripRating.class)
                .setParameter("tripId", trip.getId())
                .getResultList();

        assertEquals(1, comments.size());
        assertEquals("Great trip", comments.getFirst().getValue());
        assertEquals(4.0, rating);
        assertEquals(1, ratings.size());
        assertEquals(4, ratings.getFirst().getRating());
    }

    @Test
    void findTripByParametersReturnsMatchingTrips() {
        Category category = createCategory("Trip Category", CategoryType.TRIP);
        Tag selectedTag = createTag("selected", "integration@example.com");
        Tag otherTag = createTag("other", "integration@example.com");
        createTrip("Selected Trip", category, List.of(selectedTag), List.of(), "integration@example.com");
        createTrip("Other Trip", category, List.of(otherTag), List.of(), "integration@example.com");
        flushAndClear();

        List<Trip> trips = tripFacade.findTripByParameters(
                        new TripSearchRequest(List.of(category.getId()), List.of(selectedTag.getId()), null),
                        createPagingQueryParams()
                ).data.stream()
                .sorted(Comparator.comparing(Trip::getName))
                .toList();

        assertEquals(1, trips.size());
        assertEquals("Selected Trip", trips.getFirst().getName());
    }

    private TripCreateRequest createTripCreateRequest(Long categoryId, List<Long> tagIds, List<Long> placeIds, String name) {
        TripCreateRequest request = new TripCreateRequest();
        request.setCategoryId(categoryId);
        request.setTags(tagIds);
        request.setPlaceIds(placeIds);
        request.setName(name);
        request.setAuthor("integration@example.com");
        request.setDescription(name + " description");
        request.setDuration(180);
        request.setGeometry(createPoint(14.5, 50.15));
        return request;
    }
}
