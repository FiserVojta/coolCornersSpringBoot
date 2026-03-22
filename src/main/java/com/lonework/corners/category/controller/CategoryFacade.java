package com.lonework.corners.category.controller;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategorySearchParameters;
import com.lonework.corners.category.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryFacade {

    @Autowired
    private CategoryService categoryService;

    public Category getCategoryById(Long id) {
        return categoryService.getCategoryById(id);
    }

    public Iterable<Category> getAllCategories(CategorySearchParameters categorySearchParameters) {
        return categoryService.getAllCategories(categorySearchParameters);
    }
}
