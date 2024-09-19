package com.studies.catalog.infrastructure.category;

import com.studies.catalog.infrastructure.CategoryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategoryRepository extends ElasticsearchRepository<CategoryDocument, String> {
}
