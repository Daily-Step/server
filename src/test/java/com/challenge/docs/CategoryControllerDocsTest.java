package com.challenge.docs;

import com.challenge.api.controller.category.CategoryController;
import com.challenge.api.service.category.CategoryService;
import com.challenge.api.service.category.response.CategoryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerDocsTest extends RestDocsSupport {

    private final CategoryService categoryService = mock(CategoryService.class);

    @Override
    protected Object initController() {
        return new CategoryController(categoryService);
    }

    @DisplayName("카테고리 목록을 조회하는 API")
    @Test
    void getCategories() throws Exception {
        // given
        List<CategoryResponse> mockCategories = List.of(
                createCategoryResponse(1L, "건강"),
                createCategoryResponse(2L, "스터디")
        );
        given(categoryService.getAllCategories()).willReturn(mockCategories);

        // when // then
        mockMvc.perform(get("/api/v1/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("categories",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").description("응답 상태"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("url").description("요청 URL"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data[].id").description("카테고리 ID"),
                                fieldWithPath("data[].name").description("카테고리 이름")
                        )
                ))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.code").isEmpty())
                .andExpect(jsonPath("$.url").isEmpty())
                .andExpect(jsonPath("$.data").isArray());
    }

    private CategoryResponse createCategoryResponse(Long id, String categoryName) {
        return CategoryResponse.builder()
                .id(id)
                .name(categoryName)
                .build();
    }

}
