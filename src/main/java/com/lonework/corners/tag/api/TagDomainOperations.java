package com.lonework.corners.tag.api;

import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.tag.model.TagStatus;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TagDomainOperations implements TagOperations {

    private final EntityManager entityManager;

    public TagDomainOperations(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Tag> getTagsById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return entityManager.createQuery("select t from Tag t where t.id in (:ids)", Tag.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    @Override
    public List<Tag> resolveTagsForAssignment(List<Long> ids, Collection<Tag> currentTags) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> distinctIds = ids.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (distinctIds.size() > MAX_TAGS_PER_ENTITY) {
            throw new IllegalArgumentException("At most " + MAX_TAGS_PER_ENTITY + " tags can be assigned.");
        }
        if (distinctIds.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> currentIds = currentTags == null
                ? Set.of()
                : currentTags.stream().map(Tag::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Tag> tagsById = getTagsById(new ArrayList<>(distinctIds)).stream()
                .collect(Collectors.toMap(Tag::getId, Function.identity()));

        List<Tag> resolved = new ArrayList<>(distinctIds.size());
        for (Long id : distinctIds) {
            Tag tag = tagsById.get(id);
            if (tag == null) {
                continue;
            }
            if (tag.getStatus() == TagStatus.HIDDEN && !currentIds.contains(id)) {
                throw new IllegalArgumentException("Tag '" + tag.getName() + "' is not available.");
            }
            resolved.add(tag);
        }
        return resolved;
    }
}
