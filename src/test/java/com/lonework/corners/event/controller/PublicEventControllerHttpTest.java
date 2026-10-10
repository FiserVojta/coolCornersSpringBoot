package com.lonework.corners.event.controller;

import com.lonework.corners.support.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Verifies the query parameters the events page sends bind over HTTP. */
@AutoConfigureMockMvc
class PublicEventControllerHttpTest extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void acceptsSearchDateRangeAndOrganizerParameters() throws Exception {
        mockMvc.perform(get("/public/events")
                        .param("search", "jazz")
                        .param("startsFrom", "2026-03-19T23:00:00.000Z")
                        .param("startsUntil", "2026-03-25T22:59:59.999Z")
                        .param("createdBy", "host@example.com")
                        .param("categories", "21")
                        .param("sortBy", "START_TIME")
                        .param("sortDir", "ASC")
                        .param("page", "0")
                        .param("size", "9"))
                .andExpect(status().isOk());
    }
}
