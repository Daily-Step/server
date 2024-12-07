package com.challenge.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class DateUtils {

    /**
     * 일 포맷터
     */
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 일시 포맷터
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * LocalDate 타입을 yyyy-MM-dd 형식의 문자열로 변환한다.
     */
    public static String toDayString(LocalDate localDate) {
        return localDate.format(DAY_FORMATTER);
    }

    /**
     * LocalDateTime 타입을 yyyy-MM-dd HH:mm:ss 형식의 문자열로 변환한다.
     */
    public static String toDateTimeString(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }

}
