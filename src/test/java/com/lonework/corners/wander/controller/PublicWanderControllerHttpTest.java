package com.lonework.corners.wander.controller;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategoryType;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import com.lonework.corners.user.model.User;
import com.lonework.corners.wander.model.WanderCreateRequest;
import com.lonework.corners.wander.model.WanderType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Verifies the query parameters the cotravel page sends bind over HTTP, including search. */
@AutoConfigureMockMvc
class PublicWanderControllerHttpTest extends FacadeIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WanderFacade wanderFacade;

    @Test
    void searchParameterFiltersResults() throws Exception {
        User creator = createUser("http-search@example.com", "Http Searcher");
        Category category = createCategory("Http Wander Category", CategoryType.COTRAVEL);
        flushAndClear();
        wanderFacade.createWander(new WanderCreateRequest("Glacier lagoon paddle", 10,
                LocalDateTime.parse("2026-03-22T18:00:00"), WanderType.PUBLIC, List.of(), List.of(),
                category.getId(), List.of(), null), creator.getEmail());
        flushAndClear();

        mockMvc.perform(get("/public/wanders")
                        .param("search", "LAGOON")
                        .param("startsFrom", "2026-03-20T00:00:00")
                        .param("startsUntil", "2026-03-25T23:59:59")
                        .param("categories", String.valueOf(category.getId()))
                        .param("page", "0")
                        .param("size", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1));

        mockMvc.perform(get("/public/wanders")
                        .param("search", "no-wander-matches-this-text")
                        .param("categories", String.valueOf(category.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0));
    }
}
