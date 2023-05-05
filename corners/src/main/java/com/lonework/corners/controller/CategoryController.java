package com.lonework.corners.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lonework.corners.model.Category;
import com.lonework.corners.services.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService CategoryService;

    @CrossOrigin(origins = "*")
    @GetMapping("")
    public Iterable<Category> getPlaceById() {
        return CategoryService.getAllCategories();
    }

}
