package com.lonework.corners.tag.controller;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategoryType;
import com.lonework.corners.place.model.Place;
import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.tag.model.TagCreateRequest;
import com.lonework.corners.tag.model.TagSearchRequest;
import com.lonework.corners.tag.model.TagSuggestion;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import jakarta.persistence.PersistenceException;
import com.lonework.corners.tag.model.TagStatus;
import com.lonework.corners.tag.model.TagAdminResponse;
import com.lonework.corners.tag.model.TagCreationLimitException;
import com.lonework.corners.tag.api.TagOperations;
import com.lonework.corners.common.model.PagingQueryParams;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TagFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private TagFacade tagFacade;

    @Autowired
    private TagOperations tagOperations;

    @Test
    void createTagPersistsTrimmedNameNormalizedNameAndCallerAsCreator() {
        Tag createdTag = tagFacade.createTag(new TagCreateRequest("  Hidden   Lake "), "integration@example.com");
        flushAndClear();

        Tag persistedTag = entityManager.find(Tag.class, createdTag.getId());

        assertNotNull(persistedTag);
        assertEquals("Hidden Lake", persistedTag.getName());
        assertEquals("hiddenlake", persistedTag.getNormalizedName());
        assertEquals("integration@example.com", persistedTag.getCreator());
    }

    @Test
    void createTagReturnsExistingTagWhenNormalizedNameMatches() {
        Tag first = tagFacade.createTag(new TagCreateRequest("Night Swim"), "first@example.com");
        flushAndClear();

        Tag second = tagFacade.createTag(new TagCreateRequest("night-swim"), "second@example.com");
        flushAndClear();

        assertEquals(first.getId(), second.getId());
        assertEquals("Night Swim", second.getName());
        assertEquals("first@example.com", second.getCreator());
        assertEquals(1L, countTagsWithNormalizedName("nightswim"));
    }

    @Test
    void createTagReusesSeededSystemTag() {
        Tag tag = tagFacade.createTag(new TagCreateRequest("low cost"), "integration@example.com");
        flushAndClear();

        assertEquals("LowCost", tag.getName());
        assertEquals("system", tag.getCreator());
        assertEquals(1L, countTagsWithNormalizedName("lowcost"));
    }

    @Test
    void createTagRejectsMissingBlankOrSymbolOnlyNames() {
        assertThrows(IllegalArgumentException.class,
                () -> tagFacade.createTag(new TagCreateRequest(null), "integration@example.com"));
        assertThrows(IllegalArgumentException.class,
                () -> tagFacade.createTag(new TagCreateRequest("   "), "integration@example.com"));
        assertThrows(IllegalArgumentException.class,
                () -> tagFacade.createTag(new TagCreateRequest("!!! ---"), "integration@example.com"));
        assertThrows(IllegalArgumentException.class,
                () -> tagFacade.createTag(new TagCreateRequest("x"), "integration@example.com"));
    }

    @Test
    void createTagRejectsNameLongerThanFiftyCharacters() {
        String tooLong = "a".repeat(51);

        assertThrows(IllegalArgumentException.class,
                () -> tagFacade.createTag(new TagCreateRequest(tooLong), "integration@example.com"));
    }

    @Test
    void createTagRequiresCreator() {
        assertThrows(IllegalArgumentException.class,
                () -> tagFacade.createTag(new TagCreateRequest("waterfall"), null));
        assertThrows(IllegalArgumentException.class,
                () -> tagFacade.createTag(new TagCreateRequest("waterfall"), " "));
    }

    @Test
    void databaseRejectsTagsWithDuplicateNormalizedName() {
        createTag("Waterfall", "integration@example.com");

        assertThrows(PersistenceException.class, () -> {
            createTag("water fall", "integration@example.com");
            flushAndClear();
        });
    }

    @Test
    void getTagByIdAndGetTagsByIdReturnPersistedTags() {
        Tag hiking = createTag("hiking", "integration@example.com");
        Tag city = createTag("city", "integration@example.com");
        flushAndClear();

        Tag foundTag = tagFacade.getTagById(hiking.getId());
        List<Tag> foundTags = tagFacade.getTagsById(List.of(hiking.getId(), city.getId())).stream()
                .sorted(Comparator.comparing(Tag::getName))
                .toList();

        assertNotNull(foundTag);
        assertEquals("hiking", foundTag.getName());
        assertEquals(List.of("city", "hiking"), foundTags.stream().map(Tag::getName).toList());
    }

    @Test
    void getAllTagsReturnsPersistedTags() {
        createTag("museum", "integration@example.com");
        createTag("viewpoint", "integration@example.com");
        flushAndClear();

        List<Tag> tags = tagFacade.getAllTags().stream()
                .sorted(Comparator.comparing(Tag::getName))
                .toList();

        assertTrue(tags.stream().anyMatch(tag -> "museum".equals(tag.getName())));
        assertTrue(tags.stream().anyMatch(tag -> "viewpoint".equals(tag.getName())));
    }

    @Test
    void getTagsWithRequestReturnsDistinctTagsOfRequestedPlacesAndTrips() {
        Category category = createCategory("Nature", CategoryType.PLACE);
        Tag placeOnly = createTag("placeonly", "integration@example.com");
        Tag shared = createTag("shared", "integration@example.com");
        Tag tripOnly = createTag("triponly", "integration@example.com");
        Tag unrelated = createTag("unrelated", "integration@example.com");
        Place place = createPlace("Lake", category, List.of(placeOnly, shared), "integration@example.com");
        createPlace("Other", category, List.of(unrelated), "integration@example.com");
        var trip = createTrip("Loop", category, List.of(shared, tripOnly), List.of(), "integration@example.com");
        flushAndClear();

        List<String> both = tagNames(tagFacade.getTagsWithRequest(
                new TagSearchRequest(List.of(place.getId()), List.of(trip.getId()))));
        List<String> tripsOnly = tagNames(tagFacade.getTagsWithRequest(
                new TagSearchRequest(null, List.of(trip.getId()))));
        List<String> placesOnly = tagNames(tagFacade.getTagsWithRequest(
                new TagSearchRequest(List.of(place.getId()), List.of())));

        assertEquals(List.of("placeonly", "shared", "triponly"), both);
        assertEquals(List.of("shared", "triponly"), tripsOnly);
        assertEquals(List.of("placeonly", "shared"), placesOnly);
    }

    @Test
    void getTagsWithRequestReturnsEmptyListWhenNoIdsGiven() {
        assertTrue(tagFacade.getTagsWithRequest(new TagSearchRequest(null, null)).isEmpty());
        assertTrue(tagFacade.getTagsWithRequest(new TagSearchRequest(List.of(), List.of())).isEmpty());
    }

    @Test
    void joinTablesHaveIndexesForLookupByTag() {
        for (String table : List.of("place_has_tag", "trip_has_tag", "wander_has_tag", "travel_has_tag")) {
            Number count = (Number) entityManager.createNativeQuery("""
                            SELECT count(*)
                            FROM pg_index i
                            JOIN pg_class t ON t.oid = i.indrelid
                            JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = i.indkey[0]
                            WHERE t.relname = :table AND a.attname = 'tag_id'
                            """)
                    .setParameter("table", table)
                    .getSingleResult();

            assertTrue(count.longValue() > 0, "missing index leading with tag_id on " + table);
        }
    }

    @Test
    void searchTagsRanksPrefixMatchesFirstThenByUsageThenName() {
        Category category = createCategory("Nature", CategoryType.PLACE);
        Tag lakeView = createTag("Lake View", "integration@example.com");
        Tag lakeside = createTag("Lakeside", "integration@example.com");
        Tag hiddenLake = createTag("Hidden Lake", "integration@example.com");
        createTag("Forest", "integration@example.com");
        Place place = createPlace("Shore", category, List.of(lakeside, hiddenLake), "integration@example.com");
        createTrip("Loop", category, List.of(lakeside), List.of(place), "integration@example.com");
        flushAndClear();

        List<TagSuggestion> suggestions = tagFacade.searchTags("lake", null);

        assertEquals(List.of("Lakeside", "Lake View", "Hidden Lake"),
                suggestions.stream().map(TagSuggestion::name).toList());
        assertEquals(List.of(2L, 0L, 1L), suggestions.stream().map(TagSuggestion::usageCount).toList());
        assertEquals(lakeside.getId(), suggestions.get(0).id());
        assertEquals("lakeside", suggestions.get(0).normalizedName());
        assertEquals(lakeView.getId(), suggestions.get(1).id());
    }

    @Test
    void searchTagsNormalizesQuery() {
        createTag("Night Swim", "integration@example.com");
        flushAndClear();

        List<String> names = tagFacade.searchTags("  NIGHT-sw ", null).stream().map(TagSuggestion::name).toList();

        assertEquals(List.of("Night Swim"), names);
    }

    @Test
    void searchTagsWithoutQueryReturnsMostUsedTagsFirst() {
        Category category = createCategory("Nature", CategoryType.PLACE);
        Tag popular = createTag("zz popular", "integration@example.com");
        Tag used = createTag("zz used", "integration@example.com");
        createPlace("One", category, List.of(popular, used), "integration@example.com");
        createPlace("Two", category, List.of(popular), "integration@example.com");
        flushAndClear();

        List<TagSuggestion> blank = tagFacade.searchTags("  ", 2);
        List<TagSuggestion> missing = tagFacade.searchTags(null, 2);

        assertEquals(List.of("zz popular", "zz used"), blank.stream().map(TagSuggestion::name).toList());
        assertEquals(List.of(2L, 1L), blank.stream().map(TagSuggestion::usageCount).toList());
        assertEquals(blank, missing);
    }

    @Test
    void searchTagsAppliesDefaultAndMaximumLimit() {
        for (int i = 0; i < 60; i++) {
            createTag("bulk" + i, "integration@example.com");
        }
        flushAndClear();

        assertEquals(10, tagFacade.searchTags("bulk", null).size());
        assertEquals(3, tagFacade.searchTags("bulk", 3).size());
        assertEquals(10, tagFacade.searchTags("bulk", 0).size());
        assertEquals(50, tagFacade.searchTags("bulk", 500).size());
    }

    @Test
    void searchTagsReturnsEmptyListWhenNothingMatches() {
        assertTrue(tagFacade.searchTags("nomatchwhatsoever", null).isEmpty());
    }

    @Test
    void getTagsByNormalizedNamesResolvesNamesInRequestOrderIgnoringUnknownAndDuplicates() {
        Tag nightSwim = createTag("Night Swim", "integration@example.com");
        flushAndClear();

        List<Tag> tags = tagFacade.getTagsByNormalizedNames(
                List.of("nightswim", "Low Cost", "unknowntag", " ", "night-swim"));

        assertEquals(List.of("Night Swim", "LowCost"), tags.stream().map(Tag::getName).toList());
        assertEquals(nightSwim.getId(), tags.getFirst().getId());
    }

    @Test
    void getTagsByNormalizedNamesReturnsEmptyListForNoNames() {
        assertTrue(tagFacade.getTagsByNormalizedNames(null).isEmpty());
        assertTrue(tagFacade.getTagsByNormalizedNames(List.of()).isEmpty());
    }

    // --- Phase 4: moderation -------------------------------------------------

    @Test
    void newTagsAreActiveWithCreationTimestamp() {
        Tag tag = tagFacade.createTag(new TagCreateRequest("Moss Forest"), "integration@example.com");
        flushAndClear();

        Tag persisted = entityManager.find(Tag.class, tag.getId());
        assertEquals(TagStatus.ACTIVE, persisted.getStatus());
        assertNotNull(persisted.getCreatedAt());
    }

    @Test
    void hiddenTagsAreExcludedFromSuggestionsAndListingButStillResolve() {
        Tag hidden = createTag("Hidden Spam", "integration@example.com");
        hidden.setStatus(TagStatus.HIDDEN);
        createTag("Hidden Spring", "integration@example.com");
        flushAndClear();

        assertEquals(List.of("Hidden Spring"),
                tagFacade.searchTags("hiddensp", null).stream().map(TagSuggestion::name).toList());
        assertTrue(tagFacade.getAllTags().stream().noneMatch(tag -> "Hidden Spam".equals(tag.getName())));
        assertEquals(List.of("Hidden Spam"),
                tagFacade.getTagsByNormalizedNames(List.of("hiddenspam")).stream().map(Tag::getName).toList());
    }

    @Test
    void createTagRejectsNameOfHiddenTag() {
        Tag hidden = createTag("Spam Tag", "integration@example.com");
        hidden.setStatus(TagStatus.HIDDEN);
        flushAndClear();

        assertThrows(IllegalArgumentException.class,
                () -> tagFacade.createTag(new TagCreateRequest("spam-tag"), "integration@example.com"));
    }

    @Test
    void createTagLimitsNewTagsPerCreatorPerDayButStillReusesExistingTags() {
        for (int i = 0; i < 20; i++) {
            tagFacade.createTag(new TagCreateRequest("burst tag " + (char) ('a' + i)), "burst@example.com");
        }
        flushAndClear();

        assertThrows(TagCreationLimitException.class,
                () -> tagFacade.createTag(new TagCreateRequest("one too many"), "burst@example.com"));
        assertEquals("burst tag a",
                tagFacade.createTag(new TagCreateRequest("Burst Tag A"), "burst@example.com").getName());
        assertNotNull(tagFacade.createTag(new TagCreateRequest("someone else"), "other@example.com").getId());
    }

    @Test
    void getTagsByNormalizedNamesResolvesAtMostTwentyNames() {
        List<String> names = new java.util.ArrayList<>();
        for (int i = 0; i < 25; i++) {
            String name = "resolve" + (char) ('a' + i);
            createTag(name, "integration@example.com");
            names.add(name);
        }
        flushAndClear();

        List<Tag> tags = tagFacade.getTagsByNormalizedNames(names);

        assertEquals(names.subList(0, 20), tags.stream().map(Tag::getName).toList());
    }

    @Test
    void resolveTagsForAssignmentEnforcesLimitAndHiddenRules() {
        Tag active = createTag("assign active", "integration@example.com");
        Tag hidden = createTag("assign hidden", "integration@example.com");
        hidden.setStatus(TagStatus.HIDDEN);
        List<Long> elevenIds = new java.util.ArrayList<>();
        for (int i = 0; i < 11; i++) {
            elevenIds.add(createTag("assign cap " + (char) ('a' + i), "integration@example.com").getId());
        }
        flushAndClear();
        Tag hiddenRef = entityManager.find(Tag.class, hidden.getId());

        assertEquals(List.of(), tagOperations.resolveTagsForAssignment(null, List.of()));
        assertEquals(List.of(active.getId()), ids(tagOperations.resolveTagsForAssignment(
                List.of(active.getId(), active.getId(), 999_999L), List.of())));
        assertThrows(IllegalArgumentException.class,
                () -> tagOperations.resolveTagsForAssignment(elevenIds, List.of()));
        assertEquals(10, tagOperations.resolveTagsForAssignment(elevenIds.subList(0, 10), List.of()).size());
        assertThrows(IllegalArgumentException.class,
                () -> tagOperations.resolveTagsForAssignment(List.of(hidden.getId()), List.of()));
        assertEquals(List.of(active.getId(), hidden.getId()), ids(tagOperations.resolveTagsForAssignment(
                List.of(active.getId(), hidden.getId()), List.of(hiddenRef))));
    }

    @Test
    void adminListingFiltersByQueryAndStatusAndReportsUsage() {
        Category category = createCategory("Nature", CategoryType.PLACE);
        Tag used = createTag("admin used", "a@example.com");
        Tag hidden = createTag("admin hidden", "b@example.com");
        hidden.setStatus(TagStatus.HIDDEN);
        createTag("admin unused", "c@example.com");
        createPlace("Admin Place", category, List.of(used), "a@example.com");
        flushAndClear();

        var all = tagFacade.getTagsForAdmin("admin", null, new PagingQueryParams(0, 10, null, null));
        var hiddenOnly = tagFacade.getTagsForAdmin("admin", TagStatus.HIDDEN, new PagingQueryParams(0, 10, null, null));
        var paged = tagFacade.getTagsForAdmin("admin", null, new PagingQueryParams(1, 2, null, null));

        assertEquals(3L, all.totalItems);
        assertEquals("admin used", all.data.getFirst().name());
        assertEquals(1L, all.data.getFirst().usageCount());
        assertEquals("a@example.com", all.data.getFirst().creator());
        assertNotNull(all.data.getFirst().createdAt());
        assertEquals(List.of("admin hidden"), hiddenOnly.data.stream().map(TagAdminResponse::name).toList());
        assertEquals(TagStatus.HIDDEN, hiddenOnly.data.getFirst().status());
        assertEquals(1L, hiddenOnly.totalItems);
        assertEquals(1, paged.data.size());
        assertEquals(3L, paged.totalItems);
    }

    @Test
    void setTagStatusHidesAndUnhidesTag() {
        Tag tag = createTag("toggle me", "integration@example.com");
        flushAndClear();

        assertEquals(TagStatus.HIDDEN, tagFacade.setTagStatus(tag.getId(), TagStatus.HIDDEN).status());
        flushAndClear();
        assertEquals(TagStatus.HIDDEN, entityManager.find(Tag.class, tag.getId()).getStatus());

        assertEquals(TagStatus.ACTIVE, tagFacade.setTagStatus(tag.getId(), TagStatus.ACTIVE).status());
        assertThrows(EntityNotFoundException.class, () -> tagFacade.setTagStatus(999_999L, TagStatus.HIDDEN));
    }

    @Test
    void mergeTagsMovesLinksToTargetAndDeletesSource() {
        Category category = createCategory("Nature", CategoryType.PLACE);
        Tag source = createTag("lowcosst", "typo@example.com");
        Tag target = createTag("budget", "integration@example.com");
        Place both = createPlace("Both", category, List.of(source, target), "integration@example.com");
        Place sourceOnly = createPlace("Source Only", category, List.of(source), "integration@example.com");
        var trip = createTrip("Trip", category, List.of(source), List.of(), "integration@example.com");
        flushAndClear();

        TagAdminResponse merged = tagFacade.mergeTags(source.getId(), target.getId());
        flushAndClear();

        assertEquals(target.getId(), merged.id());
        assertEquals(3L, merged.usageCount());
        assertNull(entityManager.find(Tag.class, source.getId()));
        assertEquals(List.of("budget"), tagNames(entityManager.find(Place.class, both.getId()).getTags()));
        assertEquals(List.of("budget"), tagNames(entityManager.find(Place.class, sourceOnly.getId()).getTags()));
        assertEquals(List.of("budget"),
                tagNames(entityManager.find(com.lonework.corners.trip.model.Trip.class, trip.getId()).getTags()));
    }

    @Test
    void mergeTagsRejectsSameOrMissingTags() {
        Tag tag = createTag("solo", "integration@example.com");
        flushAndClear();

        assertThrows(IllegalArgumentException.class, () -> tagFacade.mergeTags(tag.getId(), tag.getId()));
        assertThrows(EntityNotFoundException.class, () -> tagFacade.mergeTags(tag.getId(), 999_999L));
        assertThrows(EntityNotFoundException.class, () -> tagFacade.mergeTags(999_999L, tag.getId()));
    }

    @Test
    void deleteUnusedUserTagsRemovesOnlyOldUnusedActiveUserTags() {
        Category category = createCategory("Nature", CategoryType.PLACE);
        Tag oldUnused = createTag("old unused", "user@example.com");
        Tag oldUsed = createTag("old used", "user@example.com");
        Tag oldHidden = createTag("old hidden", "user@example.com");
        oldHidden.setStatus(TagStatus.HIDDEN);
        Tag oldSystem = createTag("old system", "system");
        Tag freshUnused = createTag("fresh unused", "user@example.com");
        createPlace("Uses Old", category, List.of(oldUsed), "user@example.com");
        flushAndClear();
        entityManager.createNativeQuery("UPDATE tag SET created_at = now() - interval '40 days' WHERE id IN (:ids)")
                .setParameter("ids", List.of(oldUnused.getId(), oldUsed.getId(), oldHidden.getId(), oldSystem.getId()))
                .executeUpdate();
        flushAndClear();

        int deleted = tagFacade.deleteUnusedUserTags(java.time.Duration.ofDays(30));
        flushAndClear();

        assertEquals(1, deleted);
        assertNull(entityManager.find(Tag.class, oldUnused.getId()));
        assertNotNull(entityManager.find(Tag.class, oldUsed.getId()));
        assertNotNull(entityManager.find(Tag.class, oldHidden.getId()));
        assertNotNull(entityManager.find(Tag.class, oldSystem.getId()));
        assertNotNull(entityManager.find(Tag.class, freshUnused.getId()));
        assertEquals(TagStatus.ACTIVE, entityManager.find(Tag.class, oldSystem.getId()).getStatus());
    }

    private List<Long> ids(List<Tag> tags) {
        return tags.stream().map(Tag::getId).toList();
    }

    private long countTagsWithNormalizedName(String normalizedName) {
        return entityManager.createQuery("SELECT count(t) FROM Tag t WHERE t.normalizedName = :n", Long.class)
                .setParameter("n", normalizedName)
                .getSingleResult();
    }

    private List<String> tagNames(List<Tag> tags) {
        return tags.stream().map(Tag::getName).sorted().toList();
    }
}
