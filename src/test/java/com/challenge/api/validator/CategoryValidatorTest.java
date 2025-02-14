package com.challenge.api.validator;

import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
import com.challenge.exception.GlobalException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class CategoryValidatorTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryValidator categoryValidator;

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAllInBatch();
    }

    @DisplayName("존재하는 카테고리 ID로 조회하는 경우 예외가 발생하지 않는다.")
    @Test
    void categoryExistsBy() {
        // given
        Category category = Category.builder()
                .name("카테고리")
                .build();
        categoryRepository.save(category);

        // when // then
        assertThatCode(() -> categoryValidator.categoryExistsBy(category.getId()))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 카테고리 ID로 조회하는 경우 예외가 발생한다.")
    @Test
    void categoryDoesNotExistsBy() {
        // given
        Long notExistsCategoryId = 999L;

        // when // then
        assertThatThrownBy(() -> categoryValidator.categoryExistsBy(notExistsCategoryId))
                .isInstanceOf(GlobalException.class)
                .hasMessage("카테고리 정보를 찾을 수 없습니다. 관리자에게 문의 바랍니다.");
    }

}
