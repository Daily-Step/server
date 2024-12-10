package com.challenge.utils.date;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.challenge.utils.date.DateFormatter.LOCAL_DATE_FORMATTER;
import static com.challenge.utils.date.DateFormatter.LOCAL_DATE_TIME_FORMATTER;

public abstract class DateUtils {

    private DateUtils() {
        throw new UnsupportedOperationException("이 클래스는 유틸리티 클래스이며 인스턴스를 생성할 수 없습니다.");
    }

    /**
     * LocalDate 타입을 yyyy-MM-dd 형식의 문자열로 변환한다.
     */
    public static String toDayString(LocalDate localDate) {
        return localDate.format(LOCAL_DATE_FORMATTER);
    }

    /**
     * LocalDateTime 타입을 yyyy-MM-dd HH:mm:ss 형식의 문자열로 변환한다.
     */
    public static String toDateTimeString(LocalDateTime localDateTime) {
        return localDateTime.format(LOCAL_DATE_TIME_FORMATTER);
    }

    /**
     * yyyy-MM-dd 형식의 문자열을 LocalDate 타입으로 변환한다.
     */
    public static LocalDate toLocalDate(String s) {
        return LocalDate.parse(s, LOCAL_DATE_FORMATTER);
    }

    /**
     * yyyy-mm-dd hh24:mi:ss 형식의 문자열을 LocalDateTime 타입으로 변환한다.
     */
    public static LocalDateTime toLocalDateTime(String s) {
        return LocalDateTime.parse(s, LOCAL_DATE_TIME_FORMATTER);
    }

}
