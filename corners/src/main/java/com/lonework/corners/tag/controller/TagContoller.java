package com.lonework.corners.tag.controller;

import java.util.List;

import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.tag.model.TagCreateRequest;
import com.lonework.corners.tag.model.TagSearchRequest;
import com.lonework.corners.tag.services.TagService;

@RestController
@RequestMapping("/public/tags")
public class TagContoller {

    @Autowired
    private TagService tagService;

    @CrossOrigin(origins = "*")
    @PostMapping("/fetch")
    public Iterable<Tag> getAllTagsForPlaces(@RequestBody TagSearchRequest tagSearch) {

        return this.tagService.getTagsWithRequest(tagSearch);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("")
    public Tag createTag(@RequestBody TagCreateRequest tag) {
        return this.tagService.createTag(tag);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("")
    @PermitAll
    public List<Tag> getAllTags() {
        return this.tagService.getAllTags();
    }
}
