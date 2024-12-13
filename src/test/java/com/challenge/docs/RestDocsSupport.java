package com.challenge.docs;

import com.challenge.exception.ApiControllerAdvice;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;


/**
 * Spring REST Docs
 *
 * @see https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/#introduction
 */
@Import({RestDocsConfiguration.class})
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

    protected RestDocumentationResultHandler restDocs;

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(RestDocumentationContextProvider contextProvider) {
        objectMapper.registerModule(new JavaTimeModule());

        this.restDocs = new RestDocsConfiguration().restDocumentationResultHandler();

        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
                .setControllerAdvice(new ApiControllerAdvice())
                .apply(documentationConfiguration(contextProvider))
                .alwaysDo(MockMvcResultHandlers.print()) // print 적용
                .alwaysDo(restDocs) // RestDocsConfiguration 클래스의 bean 적용
                .build();
    }

    protected abstract Object initController();

    /**
     * 성공 공통 응답 필드 반환
     *
     * @return
     */
    protected FieldDescriptor[] successResponse() {
        return new FieldDescriptor[]{
                fieldWithPath("status").type(NUMBER).description("상태"),
                fieldWithPath("message").type(STRING).description("메시지"),
                fieldWithPath("code").type(NULL).description("코드"),
                fieldWithPath("url").type(NULL).description("API 호출 URL"),
                fieldWithPath("data").type(OBJECT).description("응답 데이터")
        };
    }

    /**
     * 실패 공통 응답 필드 반환
     *
     * @return
     */
    protected FieldDescriptor[] failResponse() {
        return new FieldDescriptor[]{
                fieldWithPath("status").type(NUMBER).description("상태 코드"),
                fieldWithPath("message").type(STRING).description("검증 실패 메시지"),
                fieldWithPath("code").type(STRING).description("에러 코드"),
                fieldWithPath("url").type(STRING).description("API 호출 URL"),
                fieldWithPath("data").type(NULL).description("응답 데이터")
        };
    }

}
