package com.challenge.domain.record;

import com.challenge.domain.BaseDateTimeEntity;
import com.challenge.domain.challenge.Challenge;
import com.challenge.utils.date.DateUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Record extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean isSucceed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Builder
    private Record(LocalDate date, boolean isSucceed, Challenge challenge) {
        this.date = date;
        this.challenge = challenge;
        this.isSucceed = isSucceed;
    }

    public static Record achieve(Challenge challenge, String achieveDate) {
        Record record = Record.builder()
                .date(DateUtils.toLocalDate(achieveDate))
                .isSucceed(true)
                .challenge(challenge)
                .build();
        challenge.addRecord(record);
        return record;
    }

    public static Record cancel(Challenge challenge, String cancelDate) {
        Record record = Record.builder()
                .date(DateUtils.toLocalDate(cancelDate))
                .isSucceed(false)
                .challenge(challenge)
                .build();
        challenge.getRecords().remove(record);
        return record;
    }

}
