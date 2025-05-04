package com.lonework.corners.category.controller;

import com.lonework.corners.category.model.CategorySearchParameters;
import com.lonework.corners.place.model.DTO.PlaceSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.services.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService CategoryService;

    @CrossOrigin(origins = "*")
    @GetMapping("")
    public Iterable<Category> getPlaceById(CategorySearchParameters categorySearchParameters) {
        return CategoryService.getAllCategories(categorySearchParameters);
    }

}
