package com.lonework.corners.seo;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategoryType;
import com.lonework.corners.event.controller.EventFacade;
import com.lonework.corners.event.model.DTO.EventCreateRequest;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import com.lonework.corners.trip.model.Trip;
import com.lonework.corners.user.model.User;
import com.lonework.corners.wander.controller.WanderFacade;
import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderCreateRequest;
import com.lonework.corners.wander.model.WanderType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contract for the public sitemap: anonymous access, XML sitemap protocol, the public list pages plus
 * every place, trip and active event. Co-travel plans, travels and user profiles are deliberately left out.
 */
@AutoConfigureMockMvc
class SitemapControllerHttpTest extends FacadeIntegrationTestSupport {

    private static final String BASE = "https://coolcorners.org";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventFacade eventFacade;

    @Autowired
    private WanderFacade wanderFacade;

    @Test
    void servesSitemapXmlAnonymously() throws Exception {
        mockMvc.perform(get("/public/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(content().string(containsString(
                        "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")))
                .andExpect(content().string(containsString("<loc>" + BASE + "/</loc>")))
                .andExpect(content().string(containsString("<loc>" + BASE + "/places</loc>")))
                .andExpect(content().string(containsString("<loc>" + BASE + "/trips</loc>")))
                .andExpect(content().string(containsString("<loc>" + BASE + "/events</loc>")))
                .andExpect(content().string(containsString("<loc>" + BASE + "/cotravel</loc>")));
    }

    @Test
    void listsPlacesTripsAndActiveEventsButNotPrivateContent() throws Exception {
        User owner = createUser("sitemap@example.com", "Sitemap Owner");
        Category placeCategory = createCategory("Sitemap Place Category", CategoryType.PLACE);
        Category eventCategory = createCategory("Sitemap Event Category", CategoryType.EVENT);
        Category cotravelCategory = createCategory("Sitemap Cotravel Category", CategoryType.COTRAVEL);
        Place place = createPlace("Sitemap Place", placeCategory, List.of(), owner.getEmail());
        Trip trip = createTrip("Sitemap Trip", placeCategory, List.of(), List.of(), owner.getEmail());
        flushAndClear();

        Event activeEvent = eventFacade.createEvent(eventRequest("Active sitemap event", owner, eventCategory), owner.getEmail());
        Event deletedEvent = eventFacade.createEvent(eventRequest("Deleted sitemap event", owner, eventCategory), owner.getEmail());
        Wander wander = wanderFacade.createWander(new WanderCreateRequest("Sitemap plan", 10,
                LocalDateTime.parse("2026-03-22T18:00:00"), WanderType.PUBLIC, List.of(), List.of(),
                cotravelCategory.getId(), List.of(), null), owner.getEmail());
        flushAndClear();
        eventFacade.deleteEvent(deletedEvent.getId());
        flushAndClear();

        String createdOn = LocalDate.now(ZoneOffset.UTC).toString();

        mockMvc.perform(get("/public/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<loc>" + BASE + "/places/" + place.getId() + "</loc>")))
                .andExpect(content().string(containsString("<loc>" + BASE + "/trips/" + trip.getId() + "</loc>")))
                .andExpect(content().string(containsString(
                        "<loc>" + BASE + "/events/" + activeEvent.getId() + "</loc><lastmod>" + createdOn + "</lastmod>")))
                .andExpect(content().string(not(containsString("/events/" + deletedEvent.getId() + "<"))))
                .andExpect(content().string(not(containsString("/cotravel/" + wander.getId() + "<"))))
                .andExpect(content().string(not(containsString("/travels"))))
                .andExpect(content().string(not(containsString("/users"))));
    }

    private EventCreateRequest eventRequest(String name, User owner, Category category) {
        return new EventCreateRequest(name, "d", "v", ZonedDateTime.parse("2026-03-22T09:00:00Z"), "09:00",
                owner.getEmail(), 10, 60, 0.0, category.getId());
    }
}
