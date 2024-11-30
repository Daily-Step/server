package com.challenge.api.controller.category;

import com.challenge.ControllerTestSupport;
import com.challenge.api.service.category.response.CategoryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest extends ControllerTestSupport {

    @DisplayName("카테고리 목록을 조회한다.")
    @Test
    void getCategories() throws Exception {
        // given
        List<CategoryResponse> mockCategories = List.of();
        given(categoryService.getAllCategories()).willReturn(mockCategories);

        // when // then
        mockMvc.perform(get("/api/v1/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.code").isEmpty())
                .andExpect(jsonPath("$.url").isEmpty())
                .andExpect(jsonPath("$.data").isArray());
    }

}
