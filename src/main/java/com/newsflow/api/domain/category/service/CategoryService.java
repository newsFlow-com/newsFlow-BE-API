package com.newsflow.api.domain.category.service;

import com.newsflow.api.domain.category.dto.CategoryResponse;
import com.newsflow.api.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllRootCategoriesWithChildren()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }
}