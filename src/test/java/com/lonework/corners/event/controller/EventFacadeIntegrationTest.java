package com.lonework.corners.event.controller;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategoryType;
import com.lonework.corners.common.model.EntityStatus;
import com.lonework.corners.common.model.PagedResult;
import com.lonework.corners.common.model.PagingQueryParams;
import com.lonework.corners.common.model.QueryOrder;
import com.lonework.corners.common.model.ResultOrder;
import com.lonework.corners.event.model.DTO.EventCreateRequest;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.event.model.EventSearchParameters;
import com.lonework.corners.support.PostgresIntegrationTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class EventFacadeIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private EventFacade eventFacade;

    @Autowired
    private EntityManager entityManager;

    @Test
    void createEventPersistsEventWithResolvedCategory() {
        Category category = createCategory();
        EventCreateRequest request = createEventCreateRequest(category.getId());

        Event createdEvent = eventFacade.createEvent(request, "integration-author@example.com");
        flushAndClear();

        Event persistedEvent = entityManager.find(Event.class, createdEvent.getId());

        assertNotNull(persistedEvent);
        assertEquals("Sunrise Walk", persistedEvent.getName());
        assertEquals("integration-author@example.com", persistedEvent.getCreatedBy());
        assertEquals(category.getId(), persistedEvent.getCategory().getId());
        assertEquals(EntityStatus.ACTIVE, persistedEvent.getEntityStatus());
    }

    @Test
    void updateEventPersistsChangedValues() {
        Category category = createCategory();
        Event createdEvent = eventFacade.createEvent(createEventCreateRequest(category.getId()), "integration-author@example.com");
        flushAndClear();

        EventCreateRequest updateRequest = createUpdatedEventCreateRequest(category.getId());

        Event updatedEvent = eventFacade.updateEvent(updateRequest, createdEvent.getId());
        flushAndClear();

        Event persistedEvent = entityManager.find(Event.class, updatedEvent.getId());

        assertNotNull(persistedEvent);
        assertEquals("Night Walk", persistedEvent.getName());
        assertEquals("Updated meetup", persistedEvent.getDescription());
        assertEquals("Updated Venue", persistedEvent.getVenue());
        assertEquals(45, persistedEvent.getCapacity());
        assertEquals(120, persistedEvent.getDuration());
        assertEquals(25.0, persistedEvent.getPrice());
    }

    @Test
    void deleteEventMarksEntityAsDeleted() {
        Category category = createCategory();
        Event createdEvent = eventFacade.createEvent(createEventCreateRequest(category.getId()), "integration-author@example.com");
        flushAndClear();

        eventFacade.deleteEvent(createdEvent.getId());
        flushAndClear();

        Event persistedEvent = entityManager.find(Event.class, createdEvent.getId());

        assertNotNull(persistedEvent);
        assertEquals(EntityStatus.DELETED, persistedEvent.getEntityStatus());
    }

    @Test
    void deleteEventThrowsWhenEntityDoesNotExist() {
        assertThrows(EntityNotFoundException.class, () -> eventFacade.deleteEvent(Long.MAX_VALUE));
    }

    @Test
    void findEventByParametersReturnsEventsMatchingRequestedFilters() {
        Category selectedCategory = createCategory("Selected Event Category");
        Category otherCategory = createCategory("Other Event Category");
        Event matchingEvent = eventFacade.createEvent(createEventCreateRequest(selectedCategory.getId()), "integration-author@example.com");
        eventFacade.createEvent(createSecondEventCreateRequest(selectedCategory.getId()), "other-author@example.com");
        eventFacade.createEvent(createDifferentCategoryEventCreateRequest(otherCategory.getId()), "integration-author@example.com");
        flushAndClear();

        eventFacade.deleteEvent(matchingEvent.getId());
        eventFacade.createEvent(createEventCreateRequest(selectedCategory.getId()), "integration-author@example.com");
        flushAndClear();

        PagedResult<Event> result = eventFacade.findEventByParameters(
                createEventSearchParameters(selectedCategory.getId()),
                createPagingQueryParams()
        );

        assertEquals(1L, result.totalItems);
        assertEquals(1, result.data.size());
        List<String> names = result.data.stream()
                .map(Event::getName)
                .sorted(Comparator.naturalOrder())
                .toList();
        assertEquals(List.of("Sunrise Walk"), names);
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setId(nextCategoryId());
        category.setName(name);
        category.setMain(true);
        category.setTitle(name);
        category.setCategoryType(CategoryType.EVENT);
        entityManager.persist(category);
        return category;
    }

    private Category createCategory() {
        return createCategory("Integration Event Category");
    }

    private Long nextCategoryId() {
        Long maxId = entityManager.createQuery("SELECT COALESCE(MAX(c.id), 0) FROM Category c", Long.class)
                .getSingleResult();
        return maxId + 1;
    }

    private static EventCreateRequest createEventCreateRequest(Long categoryId) {
        return new EventCreateRequest(
                "Sunrise Walk",
                "Morning meetup",
                "Park",
                ZonedDateTime.parse("2026-03-22T09:00:00Z"),
                "09:00",
                "ignored-by-service@example.com",
                20,
                90,
                15.0,
                categoryId
        );
    }

    private static EventCreateRequest createUpdatedEventCreateRequest(Long categoryId) {
        return new EventCreateRequest(
                "Night Walk",
                "Updated meetup",
                "Updated Venue",
                ZonedDateTime.parse("2026-03-22T19:00:00Z"),
                "19:00",
                "updated-by-request@example.com",
                45,
                120,
                25.0,
                categoryId
        );
    }

    private static EventCreateRequest createSecondEventCreateRequest(Long categoryId) {
        return new EventCreateRequest(
                "Night Walk",
                "Evening meetup",
                "Downtown",
                ZonedDateTime.parse("2026-03-22T19:30:00Z"),
                "19:30",
                "ignored-by-service@example.com",
                30,
                60,
                20.0,
                categoryId
        );
    }

    private static EventCreateRequest createDifferentCategoryEventCreateRequest(Long categoryId) {
        return new EventCreateRequest(
                "Harbor Meetup",
                "Different category event",
                "Harbor",
                ZonedDateTime.parse("2026-03-23T19:30:00Z"),
                "19:30",
                "ignored-by-service@example.com",
                18,
                75,
                10.0,
                categoryId
        );
    }

    private static EventSearchParameters createEventSearchParameters(Long categoryId) {
        return new EventSearchParameters("integration-author@example.com", "unused", List.of(categoryId), null, null);
    }

    private static PagingQueryParams createPagingQueryParams() {
        return new PagingQueryParams(0, 10, createResultOrder(), QueryOrder.ASC);
    }

    private static ResultOrder createResultOrder() {
        ResultOrder resultOrder = new ResultOrder();
        resultOrder.setOrderBy("createdAt");
        resultOrder.setOrderDirection("asc");
        return resultOrder;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
