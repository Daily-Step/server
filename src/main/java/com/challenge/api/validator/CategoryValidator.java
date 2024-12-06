package com.challenge.api.validator;

import com.challenge.domain.category.CategoryRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public void validateCategoryExists(Long categoryId) {
        boolean exists = categoryRepository.existsById(categoryId);
        if (!exists) {
            throw new GlobalException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

}
