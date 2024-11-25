package com.challenge.api.service.category;

import com.challenge.api.service.category.response.CategoryResponse;
import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
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
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(CategoryResponse::of)
                .toList();
    }

}
