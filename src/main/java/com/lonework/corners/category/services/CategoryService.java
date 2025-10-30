package com.lonework.corners.category.services;

import com.lonework.corners.category.model.Category;
import com.lonework.corners.category.model.CategorySearchParameters;
import com.lonework.corners.place.model.Place;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;


@Service
@Configurable
public class CategoryService {


    @Autowired
    private EntityManager entityManager;

    public Iterable<Category> getAllCategories(CategorySearchParameters categorySearchParameters) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(Category.class);
        Root<Category> root = query.from(Category.class);

        Predicate categoryPredicate = criteriaBuilder.conjunction();
        if(categorySearchParameters.getType() != null){
            categoryPredicate = criteriaBuilder.and(categoryPredicate,
                    criteriaBuilder.equal(root.get("categoryType"), categorySearchParameters.getType()));
        }

        query.select(root).where(categoryPredicate);

        return entityManager.createQuery(query).getResultList();
    }
}
