package com.lonework.corners.category;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategoryType;
import com.lonework.corners.event.controller.EventFacade;
import com.lonework.corners.event.model.DTO.EventCreateRequest;
import com.lonework.corners.event.model.Event;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import com.lonework.corners.travel.controller.TravelFacade;
import com.lonework.corners.travel.model.TravelCreateRequest;
import com.lonework.corners.travel.model.TravelVisibility;
import com.lonework.corners.user.model.User;
import com.lonework.corners.wander.controller.WanderFacade;
import com.lonework.corners.wander.model.Wander;
import com.lonework.corners.wander.model.WanderCreateRequest;
import com.lonework.corners.wander.model.WanderType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Covers V0.1.0.58: the final travel / cotravel / event category sets, and that rows using a removed
 * category are remapped before the category is deleted (re-running the script on recreated legacy data).
 */
class CategoryReworkMigrationIntegrationTest extends FacadeIntegrationTestSupport {

    private static final String MIGRATION =
            "db/migration/Version_0.1.x/V0.1.0.58__rework_travel_cotravel_event_categories.sql";

    private static final Set<String> TRIP_TYPE_TITLES = Set.of(
            "City break", "Nature & hiking", "Beach & islands", "Mountains & snow",
            "Culture & history", "Adventure & expedition");

    private static final Set<String> EVENT_TITLES = Set.of(
            "Talk & presentation", "Meetup & social", "Workshop", "Outdoor activity",
            "Culture & festival", "Volunteering");

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TravelFacade travelFacade;

    @Autowired
    private WanderFacade wanderFacade;

    @Autowired
    private EventFacade eventFacade;

    @Test
    void migratedCategorySetsMatchTheAgreedLists() {
        assertEquals(TRIP_TYPE_TITLES, titles("TRAVEL"));
        assertEquals(TRIP_TYPE_TITLES, titles("COTRAVEL"));
        assertEquals(EVENT_TITLES, titles("EVENT"));
        assertEquals(0L, count("SELECT count(*) FROM category WHERE category_type IS NULL"));
    }

    @Test
    void rerunningTheMigrationRemapsRowsThatUseRemovedCategories() {
        User owner = createUser("migration@example.com", "Migration Owner");
        Category travelRoadtrip = createCategory("Roadtrip", CategoryType.TRAVEL);
        Category cotravelRoadtrip = createCategory("Roadtrip", CategoryType.COTRAVEL);
        Category cotravelGroupTrip = createCategory("Group trip", CategoryType.COTRAVEL);
        Category eventTravel = createCategory("Travel", CategoryType.EVENT);
        Category untypedUsed = createCategory("View", null);
        Category untypedUnused = createCategory("Drinks", null);
        createPlace("Legacy Place", untypedUsed, List.of(), owner.getEmail());
        flushAndClear();

        Long roadtripWithoutMode = createTravel(owner);
        Long roadtripFlying = createTravel(owner);
        Long cityTravel = createTravel(owner);
        Long groupWander = createWander(owner);
        Long roadtripWander = createWander(owner);
        Long travelEvent = createEvent(owner);
        flushAndClear();
        Long cityBreak = categoryId("Citybreak", "TRAVEL");
        execute("UPDATE travel SET category_id = " + travelRoadtrip.getId() + ", transport_mode = NULL WHERE id = " + roadtripWithoutMode);
        execute("UPDATE travel SET category_id = " + travelRoadtrip.getId() + ", transport_mode = 'FLYING' WHERE id = " + roadtripFlying);
        execute("UPDATE travel SET category_id = " + cityBreak + " WHERE id = " + cityTravel);
        execute("UPDATE wander SET category_id = " + cotravelGroupTrip.getId() + " WHERE id = " + groupWander);
        execute("UPDATE wander SET category_id = " + cotravelRoadtrip.getId() + " WHERE id = " + roadtripWander);
        execute("UPDATE event SET category_id = " + eventTravel.getId() + " WHERE id = " + travelEvent);

        runMigrationScript();

        assertNull(single("SELECT category_id FROM travel WHERE id = " + roadtripWithoutMode));
        assertEquals("DRIVING", single("SELECT transport_mode FROM travel WHERE id = " + roadtripWithoutMode));
        assertNull(single("SELECT category_id FROM travel WHERE id = " + roadtripFlying));
        assertEquals("FLYING", single("SELECT transport_mode FROM travel WHERE id = " + roadtripFlying));
        assertEquals(cityBreak, ((Number) single("SELECT category_id FROM travel WHERE id = " + cityTravel)).longValue());
        assertNull(single("SELECT category_id FROM wander WHERE id = " + groupWander));
        assertNull(single("SELECT category_id FROM wander WHERE id = " + roadtripWander));
        assertEquals(categoryId("Social", "EVENT"),
                ((Number) single("SELECT category_id FROM event WHERE id = " + travelEvent)).longValue());

        for (Category removed : List.of(travelRoadtrip, cotravelRoadtrip, cotravelGroupTrip, eventTravel, untypedUnused)) {
            assertEquals(0L, count("SELECT count(*) FROM category WHERE id = " + removed.getId()), removed.getName());
        }
        assertEquals(1L, count("SELECT count(*) FROM category WHERE id = " + untypedUsed.getId()));
        assertEquals(TRIP_TYPE_TITLES, titles("TRAVEL"));
        assertEquals(TRIP_TYPE_TITLES, titles("COTRAVEL"));
        assertEquals(EVENT_TITLES, titles("EVENT"));
        assertEquals(1L, count("SELECT count(*) FROM category WHERE name = 'Mountains' AND category_type = 'TRAVEL'"));
    }

    private Long createTravel(User owner) {
        return travelFacade.createTravel(new TravelCreateRequest(
                "Legacy travel", "d", "Somewhere", LocalDate.parse("2026-01-10"), LocalDate.parse("2026-01-20"),
                TravelVisibility.PUBLIC, null, null, null, List.of(), List.of(), List.of(), null, null),
                owner.getEmail()).id();
    }

    private Long createWander(User owner) {
        Long anyCotravelCategory = categoryId("Citybreak", "COTRAVEL");
        Wander wander = wanderFacade.createWander(new WanderCreateRequest(
                "Legacy plan", 10, LocalDateTime.parse("2026-03-22T18:00:00"), WanderType.PUBLIC,
                List.of(), List.of(), anyCotravelCategory, List.of(), null), owner.getEmail());
        return wander.getId();
    }

    private Long createEvent(User owner) {
        Event event = eventFacade.createEvent(new EventCreateRequest(
                "Legacy event", "d", "v", ZonedDateTime.parse("2026-03-22T09:00:00Z"), "09:00",
                owner.getEmail(), 10, 60, 0.0, categoryId("Social", "EVENT")), owner.getEmail());
        return event.getId();
    }

    private void runMigrationScript() {
        entityManager.flush();
        ScriptUtils.executeSqlScript(DataSourceUtils.getConnection(dataSource), new ClassPathResource(MIGRATION));
        entityManager.clear();
    }

    private Set<String> titles(String type) {
        @SuppressWarnings("unchecked")
        List<String> rows = entityManager.createNativeQuery(
                        "SELECT title FROM category WHERE category_type = :type")
                .setParameter("type", type)
                .getResultList();
        return rows.stream().collect(Collectors.toSet());
    }

    private Long categoryId(String name, String type) {
        return ((Number) entityManager.createNativeQuery(
                        "SELECT id FROM category WHERE name = :name AND category_type = :type")
                .setParameter("name", name)
                .setParameter("type", type)
                .getSingleResult()).longValue();
    }

    private Object single(String sql) {
        return entityManager.createNativeQuery(sql).getSingleResult();
    }

    private long count(String sql) {
        return ((Number) single(sql)).longValue();
    }

    private void execute(String sql) {
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
