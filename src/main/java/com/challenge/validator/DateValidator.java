package com.challenge.validator;

import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.challenge.utils.date.DateFormatter.LOCAL_DATE_FORMATTER;
import static com.challenge.utils.date.DateFormatter.LOCAL_DATE_TIME_FORMATTER;

public abstract class DateValidator {

    private DateValidator() {
        throw new UnsupportedOperationException("이 클래스는 유틸리티 클래스이며 인스턴스를 생성할 수 없습니다.");
    }

    /**
     * yyyy-MM-dd 형식이 맞는지 확인한다.
     */
    public static void isLocalDateFormatter(String localDate) {
        boolean result = validDateFormatter(localDate, LOCAL_DATE_FORMATTER);
        if (!result) {
            throw new GlobalException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }

    /**
     * yyyy-MM-dd HH:mm:ss 형식을 만족하는지 확인한다.
     */
    public static void isLocalDateTimeFormatter(String localDateTime) {
        boolean result = validDateFormatter(localDateTime, LOCAL_DATE_TIME_FORMATTER);
        if (!result) {
            throw new GlobalException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }

    /**
     * 주어진 날짜가 현재 날짜와 같거나 이전 날짜인지 확인한다.
     */
    public static void isBeforeOrEqualToTodayFrom(String achieveDate) {
        LocalDate date = LocalDate.parse(achieveDate, LOCAL_DATE_FORMATTER);
        if (date.isAfter(LocalDate.now())) {
            throw new GlobalException(ErrorCode.INVALID_DATE);
        }
    }

    /**
     * 지정된 날짜 형식을 만족하는지 확인한다.
     */
    private static boolean validDateFormatter(String s, DateTimeFormatter formatter) {
        try {
            LocalDate.parse(s, formatter);
            return true;
        } catch (DateTimeParseException __) {
            return false;
        }
    }

}
