package com.challenge.utils.date;

import java.time.format.DateTimeFormatter;

public abstract class DateFormatter {

    private DateFormatter() {
        throw new UnsupportedOperationException("이 클래스는 유틸리티 클래스이며 인스턴스를 생성할 수 없습니다.");
    }

    /**
     * 년월일 포맷터
     */
    public static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 년월일 시분초 포맷터
     */
    public static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss");

}
