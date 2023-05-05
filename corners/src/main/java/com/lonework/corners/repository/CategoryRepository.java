package com.lonework.corners.repository;

import org.springframework.data.repository.CrudRepository;

import com.lonework.corners.model.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}
