package com.lonework.corners.tag.api;

import com.lonework.corners.tag.model.Tag;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
