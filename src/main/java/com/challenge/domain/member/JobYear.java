package com.challenge.domain.member;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum JobYear {

    LT_1Y(1),
    Y1_2(2),
    Y3_5(3),
    GT_6Y(4);

    private final int id;

    // id로 Year 객체 조회하기 위한 map
    private static final Map<Integer, JobYear> YEAR_MAP = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(JobYear::getId, jobYear -> jobYear));

    JobYear(int id) {
        this.id = id;
    }

    /**
     * id에 해당하는 Year Enum을 리턴하는 메소드
     *
     * @param id
     * @return
     */
    public static JobYear of(int id) {
        JobYear jobYear = YEAR_MAP.get(id);
        if (jobYear == null) {
            throw new IllegalArgumentException("Invalid id: " + id);
        }
        return jobYear;
    }

}
