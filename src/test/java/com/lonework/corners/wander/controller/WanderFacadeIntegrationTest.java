package com.lonework.corners.wander.controller;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategoryType;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.user.model.User;
import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderCreateRequest;
import com.lonework.corners.wander.model.WanderDetailResponse;
import com.lonework.corners.wander.model.WanderListResponse;
import com.lonework.corners.wander.model.WanderQueryParam;
import com.lonework.corners.wander.model.WanderType;
import jakarta.persistence.NoResultException;
import com.lonework.corners.common.model.PagingQueryParams;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class WanderFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private WanderFacade wanderFacade;

    @Test
    void createWanderPersistsResolvedRelations() {
        User creator = createUser("wanderer@example.com", "Wanderer");
        User participant = createUser("participant@example.com", "Participant");
        Category category = createCategory("Wander Category", CategoryType.COTRAVEL);
        Tag tag = createTag("sunset", "integration@example.com");
        flushAndClear();

        Wander createdWander = wanderFacade.createWander(
                createWanderCreateRequest(category.getId(), List.of(tag.getId()), List.of(participant.getId()), "Sunset wander"),
                creator.getEmail()
        );
        flushAndClear();

        Wander persistedWander = entityManager.find(Wander.class, createdWander.getId());

        assertNotNull(persistedWander);
        assertEquals("Sunset wander", persistedWander.getDescription());
        assertEquals(category.getId(), persistedWander.getCategory().getId());
        assertEquals(1, persistedWander.getTags().size());
        assertEquals(1, persistedWander.getWanderers().size());
    }

    @Test
    void updateWanderPersistsChangedValues() {
        User creator = createUser("update@example.com", "Updater");
        Category category = createCategory("Wander Category", CategoryType.COTRAVEL);
        Tag originalTag = createTag("sunset", "integration@example.com");
        Tag updatedTag = createTag("forest", "integration@example.com");
        Wander wander = wanderFacade.createWander(
                createWanderCreateRequest(category.getId(), List.of(originalTag.getId()), List.of(), "Original wander"),
                creator.getEmail()
        );
        flushAndClear();

        Wander updatedWander = wanderFacade.updateWander(
                wander.getId(),
                createWanderCreateRequest(category.getId(), List.of(updatedTag.getId()), List.of(), "Updated wander")
        );
        flushAndClear();

        Wander persistedWander = entityManager.find(Wander.class, updatedWander.getId());

        assertEquals("Updated wander", persistedWander.getDescription());
        assertEquals(1, persistedWander.getTags().size());
        assertEquals("forest", persistedWander.getTags().getFirst().getName());
    }

    @Test
    void joinAndLeaveWanderUpdateParticipants() {
        User creator = createUser("creator@example.com", "Creator");
        User participant = createUser("joiner@example.com", "Joiner");
        Category category = createCategory("Wander Category", CategoryType.COTRAVEL);
        Wander wander = wanderFacade.createWander(
                createWanderCreateRequest(category.getId(), List.of(), List.of(), "Join wander"),
                creator.getEmail()
        );
        flushAndClear();

        wanderFacade.joinWander(wander.getId(), participant.getEmail());
        flushAndClear();

        Wander joinedWander = entityManager.find(Wander.class, wander.getId());
        assertEquals(1, joinedWander.getWanderers().size());

        wanderFacade.leaveWander(wander.getId(), participant.getEmail());
        flushAndClear();

        Wander leftWander = entityManager.find(Wander.class, wander.getId());
        assertEquals(0, leftWander.getWanderers().size());
    }

    @Test
    void getWanderAndGetWanderListReturnPersistedWanders() {
        User creator = createUser("list@example.com", "Lister");
        Category category = createCategory("Wander Category", CategoryType.COTRAVEL);
        Wander wander = wanderFacade.createWander(
                createWanderCreateRequest(category.getId(), List.of(), List.of(), "Listed wander"),
                creator.getEmail()
        );
        flushAndClear();

        WanderDetailResponse detail = wanderFacade.getWander(wander.getId());
        List<WanderListResponse> wanders = wanderFacade.getWanderListResponse(
                createPagingQueryParams(),
                new WanderQueryParam(null, null, creator.getId(), List.of(category.getId()), List.of(), List.of(), null, null, null)
        ).data;

        assertEquals("Listed wander", detail.description());
        assertEquals(1, wanders.size());
        assertEquals(wander.getId(), wanders.getFirst().id());
    }

    @Test
    void getWanderListRequiresAllSelectedTagsAndReturnsEachWanderOnce() {
        User creator = createUser("tags@example.com", "Tagger");
        Category category = createCategory("Wander Category", CategoryType.COTRAVEL);
        Tag sunset = createTag("sunset", "integration@example.com");
        Tag forest = createTag("forest", "integration@example.com");
        flushAndClear();
        Wander both = wanderFacade.createWander(
                createWanderCreateRequest(category.getId(), List.of(sunset.getId(), forest.getId()), List.of(), "Both tags"),
                creator.getEmail());
        Wander sunsetOnly = wanderFacade.createWander(
                createWanderCreateRequest(category.getId(), List.of(sunset.getId()), List.of(), "Sunset only"),
                creator.getEmail());
        wanderFacade.createWander(
                createWanderCreateRequest(category.getId(), List.of(forest.getId()), List.of(), "Forest only"),
                creator.getEmail());
        flushAndClear();

        var bothResult = wanderFacade.getWanderListResponse(createPagingQueryParams(),
                new WanderQueryParam(null, null, null, null, List.of(sunset.getId(), forest.getId()), List.of(), null, null, null));
        var sunsetResult = wanderFacade.getWanderListResponse(createPagingQueryParams(),
                new WanderQueryParam(null, null, null, null, List.of(sunset.getId(), sunset.getId()), List.of(), null, null, null));

        assertEquals(List.of(both.getId()), bothResult.data.stream().map(WanderListResponse::id).toList());
        assertEquals(1L, bothResult.totalItems);
        assertEquals(List.of(both.getId(), sunsetOnly.getId()).stream().sorted().toList(),
                sunsetResult.data.stream().map(WanderListResponse::id).sorted().toList());
        assertEquals(2L, sunsetResult.totalItems);
    }

    @Test
    void getWanderListReturnsDisjointPagesCoveringAllMatches() {
        User creator = createUser("pager@example.com", "Pager");
        Category category = createCategory("Wander Category", CategoryType.COTRAVEL);
        flushAndClear();
        List<Long> created = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            created.add(wanderFacade.createWander(
                    createWanderCreateRequest(category.getId(), List.of(), List.of(), "Paged wander " + i),
                    creator.getEmail()).getId());
        }
        flushAndClear();
        WanderQueryParam filter = new WanderQueryParam(null, null, creator.getId(), null, null, List.of(), null, null, null);

        List<Long> seen = new ArrayList<>();
        for (int page = 0; page < 3; page++) {
            var result = wanderFacade.getWanderListResponse(new PagingQueryParams(page, 2, null, null), filter);
            assertEquals(5L, result.totalItems);
            assertEquals(page < 2 ? 2 : 1, result.data.size());
            result.data.forEach(wander -> seen.add(wander.id()));
        }

        assertEquals(created.stream().sorted().toList(), seen.stream().sorted().toList());
    }

    @Test
    void createWanderRejectsMoreThanTenTags() {
        User creator = createUser("cap@example.com", "Capper");
        Category category = createCategory("Wander Category", CategoryType.COTRAVEL);
        List<Long> elevenIds = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            elevenIds.add(createTag("wander cap " + (char) ('a' + i), "integration@example.com").getId());
        }
        flushAndClear();

        assertThrows(IllegalArgumentException.class, () -> wanderFacade.createWander(
                createWanderCreateRequest(category.getId(), elevenIds, List.of(), "Too many"), creator.getEmail()));
    }

    @Test
    void getWanderListSearchesDescriptionAndStopNamesCaseInsensitively() {
        User creator = createUser("search@example.com", "Searcher");
        Category category = createCategory("Wander Category", CategoryType.COTRAVEL);
        flushAndClear();
        Wander byDescription = wanderFacade.createWander(
                createWanderCreateRequest(category.getId(), List.of(), List.of(), "Sunset kayak trip"), creator.getEmail());
        Wander byStop = wanderFacade.createWander(new WanderCreateRequest(
                "Weekend outing", 10, LocalDateTime.parse("2026-03-22T18:00:00"), WanderType.PUBLIC, List.of(),
                List.of(), category.getId(),
                List.of(new com.lonework.corners.wander.model.WanderPartCreateRequest(List.of(), List.of(), 1, "Old Town KAYAK rental"),
                        new com.lonework.corners.wander.model.WanderPartCreateRequest(List.of(), List.of(), 2, "Kayak return")),
                null), creator.getEmail());
        Wander percent = wanderFacade.createWander(
                createWanderCreateRequest(category.getId(), List.of(), List.of(), "100% fun hike"), creator.getEmail());
        wanderFacade.createWander(
                createWanderCreateRequest(category.getId(), List.of(), List.of(), "Museum day"), creator.getEmail());
        flushAndClear();

        var kayak = wanderFacade.getWanderListResponse(createPagingQueryParams(),
                new WanderQueryParam(null, null, creator.getId(), null, null, List.of(), null, null, "  kAyAk "));
        var literalPercent = wanderFacade.getWanderListResponse(createPagingQueryParams(),
                new WanderQueryParam(null, null, creator.getId(), null, null, List.of(), null, null, "%"));
        var blank = wanderFacade.getWanderListResponse(createPagingQueryParams(),
                new WanderQueryParam(null, null, creator.getId(), null, null, List.of(), null, null, " "));

        assertEquals(List.of(byDescription.getId(), byStop.getId()).stream().sorted().toList(),
                kayak.data.stream().map(WanderListResponse::id).sorted().toList());
        assertEquals(2L, kayak.totalItems);
        assertEquals(List.of(percent.getId()), literalPercent.data.stream().map(WanderListResponse::id).toList());
        assertEquals(4L, blank.totalItems);
    }

    @Test
    void deleteWanderRemovesEntity() {
        User creator = createUser("delete@example.com", "Deleter");
        Category category = createCategory("Wander Category", CategoryType.COTRAVEL);
        Wander wander = wanderFacade.createWander(
                createWanderCreateRequest(category.getId(), List.of(), List.of(), "Delete wander"),
                creator.getEmail()
        );
        flushAndClear();

        wanderFacade.deleteWander(wander.getId());
        flushAndClear();

        assertThrows(NoResultException.class, () -> wanderFacade.getWander(wander.getId()));
    }

    private WanderCreateRequest createWanderCreateRequest(Long categoryId, List<Long> tagIds, List<Long> wandererIds, String description) {
        return new WanderCreateRequest(
                description,
                10,
                LocalDateTime.parse("2026-03-22T18:00:00"),
                WanderType.PUBLIC,
                wandererIds,
                tagIds,
                categoryId,
                List.of(),
                null
        );
    }
}
