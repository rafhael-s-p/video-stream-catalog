package com.studies.catalog.infrastructure.category;

import com.studies.catalog.domain.category.Category;
import com.studies.catalog.domain.category.CategoryGateway;
import com.studies.catalog.domain.category.CategorySearchQuery;
import com.studies.catalog.domain.pagination.Pagination;
import com.studies.catalog.infrastructure.category.persistence.CategoryDocument;
import com.studies.catalog.infrastructure.category.persistence.CategoryRepository;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class CategoryElasticsearchGateway implements CategoryGateway {

    private final CategoryRepository categoryRepository;
    private final SearchOperations searchOperations;

    public CategoryElasticsearchGateway(
            final CategoryRepository categoryRepository,
            final SearchOperations searchOperations
    ) {
        this.categoryRepository = Objects.requireNonNull(categoryRepository);
        this.searchOperations = Objects.requireNonNull(searchOperations);
    }

    @Override
    public Category save(Category aCategory) {
        this.categoryRepository.save(CategoryDocument.from(aCategory));
        return aCategory;
    }

    @Override
    public void deleteById(String anId) {
        this.categoryRepository.deleteById(anId);
    }

    @Override
    public Optional<Category> findById(String anId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pagination<Category> findAll(CategorySearchQuery aQuery) {
        throw new UnsupportedOperationException();
    }
}
