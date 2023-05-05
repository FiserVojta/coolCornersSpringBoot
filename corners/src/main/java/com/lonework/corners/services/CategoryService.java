package com.lonework.corners.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.lonework.corners.model.Category;
import com.lonework.corners.repository.CategoryRepository;

@Service
@Configurable
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public Iterable<Category> getAllCategories() {
        return this.repository.findAll();
    }
}
