package com.lonework.corners.tag.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.tag.model.Tag;
import com.lonework.corners.tag.model.TagSearchRequest;
import com.lonework.corners.tag.model.TagSuggestion;

@RestController
@RequestMapping("/public/tags")
public class TagController {

    @Autowired
    private TagFacade tagFacade;

    @PostMapping("/fetch")
    public Iterable<Tag> getAllTagsForPlaces(@RequestBody TagSearchRequest tagSearch) {

        return tagFacade.getTagsWithRequest(tagSearch);
    }

    @GetMapping("")
    public List<Tag> getAllTags() {
        return tagFacade.getAllTags();
    }

    @GetMapping("/search")
    public List<TagSuggestion> searchTags(@RequestParam(name = "q", required = false) String q,
                                          @RequestParam(name = "limit", required = false) Integer limit) {
        return tagFacade.searchTags(q, limit);
    }

    @GetMapping("/resolve")
    public List<Tag> resolveTags(@RequestParam(name = "names", required = false) List<String> names) {
        return tagFacade.getTagsByNormalizedNames(names);
    }
}
