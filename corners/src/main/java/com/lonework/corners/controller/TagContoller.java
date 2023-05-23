package com.lonework.corners.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.model.City;
import com.lonework.corners.model.Place;
import com.lonework.corners.model.Tag;
import com.lonework.corners.model.request.TagSearchRequest;
import com.lonework.corners.services.CityService;
import com.lonework.corners.services.TagService;

@RestController
@RequestMapping("/tag")
public class TagContoller {

    @Autowired
    private TagService tagService;

    @CrossOrigin(origins = "*")
    @PostMapping("/fetch")
    public Iterable<Tag> getAllTagsForPlaces(@RequestBody TagSearchRequest places) {

        return this.tagService.getAllTagsForPlaces(places);
    }
}
