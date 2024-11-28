package com.challenge.domain.member;

import lombok.Getter;

@Getter
public enum Year {

    LT_1Y(1),
    Y1_2(2),
    Y3_5(3),
    GT_6Y(4);

    private final int id;

    Year(int id) {
        this.id = id;
    }
}
