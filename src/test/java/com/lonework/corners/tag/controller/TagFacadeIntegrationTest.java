package com.lonework.corners.tag.controller;

import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.tag.model.TagCreateRequest;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TagFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private TagFacade tagFacade;

    @Test
    void createTagPersistsTag() {
        TagCreateRequest request = new TagCreateRequest();
        request.setName("waterfall");
        request.setCreator("integration@example.com");

        Tag createdTag = tagFacade.createTag(request);
        flushAndClear();

        Tag persistedTag = entityManager.find(Tag.class, createdTag.getId());

        assertNotNull(persistedTag);
        assertEquals("waterfall", persistedTag.getName());
        assertEquals("integration@example.com", persistedTag.getCreator());
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
}
