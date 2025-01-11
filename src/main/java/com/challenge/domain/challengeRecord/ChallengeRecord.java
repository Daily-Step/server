package com.challenge.domain.challengeRecord;

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
public class ChallengeRecord extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column(nullable = false)
    private boolean isSucceed;

    @Builder
    private ChallengeRecord(LocalDate recordDate, boolean isSucceed, Challenge challenge) {
        this.recordDate = recordDate;
        this.challenge = challenge;
        this.isSucceed = isSucceed;
    }

    public static ChallengeRecord achieve(Challenge challenge, String achieveDate) {
        ChallengeRecord achieveRecord = ChallengeRecord.builder()
                .recordDate(DateUtils.toLocalDate(achieveDate))
                .isSucceed(true)
                .challenge(challenge)
                .build();
        challenge.addRecord(achieveRecord);
        return achieveRecord;
    }

    public static ChallengeRecord cancel(Challenge challenge, String cancelDate) {
        return ChallengeRecord.builder()
                .recordDate(DateUtils.toLocalDate(cancelDate))
                .isSucceed(false)
                .challenge(challenge)
                .build();
    }

}
