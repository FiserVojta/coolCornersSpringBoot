package com.lonework.corners.category.controller;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategorySearchParameters;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/categories")
public class CategoryController {

    @Autowired
    private CategoryFacade categoryFacade;

    @GetMapping("")
    @PermitAll
    public Iterable<Category> getPlaceById(CategorySearchParameters categorySearchParameters) {
        return categoryFacade.getAllCategories(categorySearchParameters);
    }

}
