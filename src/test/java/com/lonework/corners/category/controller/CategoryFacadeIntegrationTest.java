package com.lonework.corners.category.controller;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategorySearchParameters;
import com.lonework.corners.category.model.CategoryType;
import com.lonework.corners.support.FacadeIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class CategoryFacadeIntegrationTest extends FacadeIntegrationTestSupport {

    @Autowired
    private CategoryFacade categoryFacade;

    @Test
    void getCategoryByIdReturnsPersistedCategory() {
        Category category = createCategory("Category Detail", CategoryType.PLACE);
        flushAndClear();

        Category foundCategory = categoryFacade.getCategoryById(category.getId());

        assertNotNull(foundCategory);
        assertEquals(category.getId(), foundCategory.getId());
        assertEquals("Category Detail", foundCategory.getName());
    }

    @Test
    void getAllCategoriesFiltersByType() {
        createCategory("Place Category", CategoryType.PLACE);
        createCategory("Trip Category", CategoryType.TRIP);
        flushAndClear();

        CategorySearchParameters searchParameters = new CategorySearchParameters();
        searchParameters.setType(CategoryType.PLACE.name());

        List<Category> categories = (List<Category>) categoryFacade.getAllCategories(searchParameters);

        assertEquals(true, categories.stream().allMatch(category -> category.getCategoryType() == CategoryType.PLACE));
        assertEquals(true, categories.stream().anyMatch(category -> "Place Category".equals(category.getName())));
    }
}
