package com.lonework.corners.services;

import com.lonework.corners.model.City;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import com.lonework.corners.model.Tag;
import com.lonework.corners.model.request.TagCreateRequest;
import com.lonework.corners.model.request.TagSearchRequest;
import com.lonework.corners.repository.TagRepository;

import java.util.List;
import java.util.UUID;


@Service
@Configurable
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    EntityManager entityManager;



    public Iterable<Tag> getTagsWithRequest(TagSearchRequest tagSearchRequest) {
        return tagRepository.findAllByPlaceIds(tagSearchRequest.getPlaceId(), tagSearchRequest.getTripId());
    }

    @Transactional
    public Tag createTag(TagCreateRequest tagCreateRequest) {
        Tag tag = new Tag();
        tag.setCreator(tagCreateRequest.creator);
        tag.setName(tagCreateRequest.name);
        return  entityManager.merge(tag);
    }

    public List<Tag> getAllTags() {
        return entityManager.createQuery("SELECT t FROM Tag t", Tag.class).getResultList();
    }

}
