package com.lonework.corners.category.services;

import com.lonework.corners.category.model.Category;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;


@Service
@Configurable
public class CategoryService {


    @Autowired
    private EntityManager entityManager;

    public Iterable<Category> getAllCategories() {
        return this.entityManager.createQuery("select c from Category c", Category.class).getResultList();
    }
}
