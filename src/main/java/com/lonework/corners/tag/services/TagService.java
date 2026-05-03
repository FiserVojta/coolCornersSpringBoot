package com.lonework.corners.tag.services;

import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.tag.model.TagCreateRequest;
import com.lonework.corners.tag.model.TagSearchRequest;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Configurable
public class TagService {


    @Autowired
    EntityManager entityManager;


    public List<Tag> getTagsWithRequest(TagSearchRequest tagSearchRequest) {
        return entityManager.createQuery("SELECT t FROM Tag t JOIN t.places p JOIN t.trips tr WHERE (p.id IN :placeIds OR tr.id IN :tripIds)", Tag.class)
                .getResultList();
    }

    @Transactional
    public Tag createTag(TagCreateRequest tagCreateRequest) {
        Tag tag = new Tag();
        tag.setCreator(tagCreateRequest.creator());
        tag.setName(tagCreateRequest.name());
        return entityManager.merge(tag);
    }

    public List<Tag> getAllTags() {
        return entityManager.createQuery("SELECT t FROM Tag t", Tag.class).getResultList();
    }



}
