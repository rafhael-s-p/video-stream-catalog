package com.studies.catalog.domain.category;

import com.studies.catalog.domain.pagination.Pagination;

import java.util.Optional;

public interface CategoryGateway {

    Category save(Category aCategory);

    void deleteById(String anId);

    Optional<Category> findById(String anId);

    Pagination<Category> findAll(CategorySearchQuery aQuery);

}
