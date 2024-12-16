package com.challenge.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /**
     * 공통으로 사용되는 일반적인 에러
     */
    COMMON_BAD_REQUEST(BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    COMMON_UNAUTHORIZED(UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    COMMON_FORBIDDEN(FORBIDDEN, "COMMON_403", "금지된 요청입니다."),
    COMMON_INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러, 관리자에게 문의 바랍니다."),
    COMMON_NETWORK_ERROR(GATEWAY_TIMEOUT, "COMMON_504", "네트워크 오류가 발생하였습니다. 잠시 후 다시 시도해주세요."),

    /**
     * 인증 관련 에러
     */
    MISSING_AUTH_HEADER(UNAUTHORIZED, "AUTH_4001", "Authorization 헤더가 없습니다."),
    INVALID_AUTH_HEADER(UNAUTHORIZED, "AUTH_4002", "Authorization 헤더가 올바르지 않습니다."),
    INVALID_SIGNATURE(UNAUTHORIZED, "AUTH_4003", "JWT 서명이 유효하지 않습니다."),
    MALFORMED_TOKEN(UNAUTHORIZED, "AUTH_4004", "JWT의 형식이 올바르지 않습니다."),
    UNSUPPORTED_TOKEN(UNAUTHORIZED, "AUTH_4005", "지원되지 않는 JWT입니다."),
    EXPIRED_JWT_EXCEPTION(UNAUTHORIZED, "AUTH_4006", "기존 토큰이 만료되었습니다. 토큰을 재발급해주세요."),
    INVALID_CLAIMS(UNAUTHORIZED, "AUTH_4007", "JWT의 클레임이 유효하지 않습니다."),
    EXPIRED_REFRESH_TOKEN(BAD_REQUEST, "AUTH_4008", "리프레쉬 토큰이 만료되었습니다. 다시 로그인 해주세요"),
    UNAUTHORIZED_EXCEPTION(UNAUTHORIZED, "AUTH_4009", "로그인 후 이용가능합니다. 토큰을 입력해 주세요"),
    MEMBER_EXTRACTION_FAILED(NOT_FOUND, "AUTH_4010", "회원 정보를 추출할 수 없습니다."),
    INACTIVE_MEMBER(NOT_FOUND, "AUTH_4011", "탈퇴한 사용자 입니다."),

    /**
     * 소셜 로그인 관련 에러
     */
    KAKAO_REQ_FAILED(BAD_REQUEST, "AUTH_4007", "카카오 access token으로 사용자 정보 요청에 실패했습니다."),

    /**
     * 회원 관련 에러
     */
    MEMBER_NOT_FOUND(NOT_FOUND, "MEMBER_4001", "사용자를 찾을 수 없습니다."),
    MEMBER_EXISTS(BAD_REQUEST, "MEMBER_4002", "이미 존재하는 회원입니다."),
    DUPLICATED_NICKNAME(BAD_REQUEST, "MEMBER_4003", "이미 사용중인 닉네임입니다."),

    /**
     * 사용자 관련 에러
     */
    USER_DUPLICATE_LOGIN_ID(UNPROCESSABLE_ENTITY, "USER_4001", "이미 존재하는 아이디입니다."),

    /**
     * 챌린지 관련 에러
     */
    CHALLENGE_NOT_FOUND(NOT_FOUND, "CHALLENGE_4001", "챌린지 정보를 찾을 수 없습니다. 관리자에게 문의 바랍니다."),

    /**
     * 카테고리 관련 에러
     */
    CATEGORY_NOT_FOUND(NOT_FOUND, "CATEGORY_4001", "카테고리 정보를 찾을 수 없습니다. 관리자에게 문의 바랍니다."),

    /**
     * 기록 관련 에러
     */
    DUPLICATE_RECORD(BAD_REQUEST, "RECORD_4001", "오늘 이미 해당 챌린지를 달성했습니다."),
    RECORD_NOT_FOUND(NOT_FOUND, "RECORD_4002", "기록 정보를 찾을 수 없습니다. 관리자에게 문의 바랍니다."),

    /**
     * 날짜 관련 에러
     */
    INVALID_DATE_FORMAT(BAD_REQUEST, "DATE_4001", "날짜 형식이 올바르지 않습니다."),
    INVALID_DATE(BAD_REQUEST, "DATE_4002", "날짜가 올바르지 않습니다."),

    /**
     * 기타 에러
     */
    JOB_NOT_FOUND(NOT_FOUND, "ERROR_4001", "직무 정보를 찾을 수 없습니다. 관리자에게 문의 바랍니다."),
    S3_UPLOAD_ERROR(INTERNAL_SERVER_ERROR, "ERROR_4002", "이미지 업로드에 실패했습니다. 관리자에게 문의 바랍니다.");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
}

