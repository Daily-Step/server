package com.challenge.api.controller;

import com.challenge.annotation.resolver.AuthMemberArgumentResolver;
import com.challenge.api.interceptor.AuthInterceptor;
import com.challenge.domain.job.Job;
import com.challenge.domain.member.Gender;
import com.challenge.domain.member.JobYear;
import com.challenge.domain.member.LoginType;
import com.challenge.domain.member.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@WebMvcTest
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected AuthInterceptor authInterceptor;

    @MockitoBean
    protected AuthMemberArgumentResolver authMemberArgumentResolver;

    @MockitoBean
    protected JPAQueryFactory queryFactory;

    @Autowired
    protected ObjectMapper objectMapper;

    protected static final Long MOCK_SOCIAL_ID = 1L;
    protected static final String MOCK_EMAIL = "test@naver.com";
    protected static final String MOCK_NICKNAME = "test";
    protected static final String MOCK_BIRTH = LocalDate.of(2000, 1, 1).toString();
    protected static final Gender MOCK_GENDER = Gender.MALE;
    protected static final JobYear MOCK_JOBYEAR = JobYear.LT_1Y;
    protected static final Job MOCK_JOB = Job.builder().id(1L).code("1").description("1").build();

    protected Member mockMember;

    @BeforeEach
    public void baseSetUp() throws Exception {
        // 인터셉터가 항상 true를 반환하도록 Mock 설정
        given(authInterceptor.preHandle(any(), any(), any())).willReturn(true);

        // 리졸버가 mockMember를 반환하도록 Mock 설정

        mockMember = Member.builder()
                .socialId(MOCK_SOCIAL_ID)
                .email(MOCK_EMAIL)
                .loginType(LoginType.KAKAO)
                .nickname(MOCK_NICKNAME)
                .birth(LocalDate.of(2000, 1, 1))
                .gender(MOCK_GENDER)
                .jobYear(MOCK_JOBYEAR)
                .job(MOCK_JOB)
                .build();
        given(authMemberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(mockMember);
    }

}
