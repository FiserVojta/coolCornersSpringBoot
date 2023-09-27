package com.lonework.corners.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.lonework.corners.model.Place;
import com.lonework.corners.model.State;
import com.lonework.corners.model.Tag;
import com.lonework.corners.model.request.TagCreateRequest;
import com.lonework.corners.model.request.TagSearchRequest;
import com.lonework.corners.repository.StateRepository;
import com.lonework.corners.repository.TagRepository;

@Service
@Configurable
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public Iterable<Tag> getTagsWithRequest(TagSearchRequest tagSearchRequest) {

        return tagRepository.findAllByPlaceIds(tagSearchRequest.getPlaceId(), tagSearchRequest.getTripId());
    }

    public Tag createTag(TagCreateRequest tagCreateRequest) {
        Tag tag = new Tag(tagCreateRequest);
        Tag response = tagRepository.save(tag);
        return response;
    }

    public Iterable<Tag> getAllTags() {

        return this.tagRepository.findAll();
    }

}
