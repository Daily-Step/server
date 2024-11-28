package com.challenge.domain.member;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum Year {

    LT_1Y(1),
    Y1_2(2),
    Y3_5(3),
    GT_6Y(4);

    private final int id;

    // id로 Year 객체 조회하기 위한 map
    private static final Map<Integer, Year> YEAR_MAP = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(Year::getId, year -> year));

    Year(int id) {
        this.id = id;
    }

    /**
     * id에 해당하는 Year Enum을 리턴하는 메소드
     *
     * @param id
     * @return
     */
    public static Year of(int id) {
        Year year = YEAR_MAP.get(id);
        if (year == null) {
            throw new IllegalArgumentException("Invalid id: " + id);
        }
        return year;
    }

}
