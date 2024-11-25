package com.challenge.api.service.category.response;

import com.challenge.domain.category.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryResponse {

    private final Long id;
    private final String title;

    @Builder
    private CategoryResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public static CategoryResponse of(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .title(category.getTitle())
                .build();
    }

}
