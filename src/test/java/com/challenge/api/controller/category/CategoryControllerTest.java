package com.challenge.api.controller.category;

import com.challenge.api.controller.ControllerTestSupport;
import com.challenge.api.service.category.CategoryService;
import com.challenge.api.service.category.response.CategoryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerTest extends ControllerTestSupport {

    @MockBean
    private CategoryService categoryService;

    @DisplayName("카테고리 목록을 조회한다.")
    @Test
    void getCategories() throws Exception {
        // given
        List<CategoryResponse> mockCategories = List.of();
        when(categoryService.getAllCategories()).thenReturn(mockCategories);

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
