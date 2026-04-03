package com.lonework.corners.category.api;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.services.CategoryService;
import org.springframework.stereotype.Component;

@Component
public class CategoryDomainOperations implements CategoryOperations {

    private final CategoryService categoryService;

    public CategoryDomainOperations(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryService.getCategoryById(id);
    }
}
