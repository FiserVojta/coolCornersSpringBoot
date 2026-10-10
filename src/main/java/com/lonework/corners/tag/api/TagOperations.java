package com.lonework.corners.tag.api;

import com.lonework.corners.tag.model.Tag;

import java.util.Collection;
import java.util.List;

public interface TagOperations {

    int MAX_TAGS_PER_ENTITY = 10;

    List<Tag> getTagsById(List<Long> ids);

    /**
     * Resolves the tag ids requested for a place/trip/wander/travel. Duplicates and unknown ids are
     * dropped and request order is kept. More than {@value #MAX_TAGS_PER_ENTITY} distinct ids, or a
     * HIDDEN tag that is not already in {@code currentTags}, is rejected with IllegalArgumentException.
     *
     * @param currentTags the entity's tags before the update (empty for a create)
     * @return a new mutable list of resolved tags
     */
    List<Tag> resolveTagsForAssignment(List<Long> ids, Collection<Tag> currentTags);
}
