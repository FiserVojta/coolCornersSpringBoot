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
                new WanderQueryParam(null, null, creator.getId(), List.of(category.getId()), List.of(), List.of())
        ).data;

        assertEquals("Listed wander", detail.description());
        assertEquals(1, wanders.size());
        assertEquals(wander.getId(), wanders.getFirst().id());
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
