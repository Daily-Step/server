package com.challenge.api.service.category;

import com.challenge.api.service.category.response.CategoryResponse;
import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@ActiveProfiles("test")
@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("카테고리 목록을 조회한다.")
    @Test
    void getAllCategories() {
        // given
        Category category1 = createCategory("공부");
        Category category2 = createCategory("운동");
        Category category3 = createCategory("프로젝트");
        categoryRepository.saveAll(List.of(category1, category2, category3));

        // when
        List<CategoryResponse> categoryResponses = categoryService.getAllCategories();

        // then
        assertThat(categoryResponses).hasSize(3)
                .extracting("name")
                .containsExactly("공부", "운동", "프로젝트");

        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(3)
                .extracting("name", "isDeleted")
                .containsExactlyInAnyOrder(
                        tuple("공부", false),
                        tuple("운동", false),
                        tuple("프로젝트", false)
                );
    }

    private Category createCategory(String name) {
        return Category.builder()
                .name(name)
                .isDeleted(false)
                .build();
    }

}
