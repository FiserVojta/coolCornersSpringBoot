package com.lonework.corners.tag.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.tag.model.TagCreateRequest;
import com.lonework.corners.tag.model.TagSearchRequest;

@RestController
@RequestMapping("/public/tags")
public class TagContoller {

    @Autowired
    private TagFacade tagFacade;

    @PostMapping("/fetch")
    public Iterable<Tag> getAllTagsForPlaces(@RequestBody TagSearchRequest tagSearch) {

        return tagFacade.getTagsWithRequest(tagSearch);
    }

    @PostMapping("")
    public Tag createTag(@RequestBody TagCreateRequest tag) {
        return tagFacade.createTag(tag);
    }

    @GetMapping("")
    public List<Tag> getAllTags() {
        return tagFacade.getAllTags();
    }
}
