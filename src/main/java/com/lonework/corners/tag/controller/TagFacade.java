package com.lonework.corners.tag.controller;


import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.tag.model.TagCreateRequest;
import com.lonework.corners.tag.model.TagSearchRequest;
import com.lonework.corners.tag.services.TagService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TagFacade {

    @Autowired
    TagService tagService;

    @Autowired
    EntityManager entityManager;

    public Tag getTagById(Long id) {
        return entityManager.find(Tag.class, id);
    }

    public List<Tag> getTagsById(List<Long> id) {
        return entityManager.createQuery("select t from Tag t where t.id in (:ids)", Tag.class)
                .setParameter("ids", id)
                .getResultList();
    }

    public List<Tag> getTagsWithRequest(TagSearchRequest tagSearchRequest) {
        return tagService.getTagsWithRequest(tagSearchRequest);
    }

    public Tag createTag(TagCreateRequest tagCreateRequest) {
        return tagService.createTag(tagCreateRequest);
    }

    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }

}
