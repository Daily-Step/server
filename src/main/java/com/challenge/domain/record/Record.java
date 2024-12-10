package com.challenge.domain.record;

import com.challenge.domain.BaseDateTimeEntity;
import com.challenge.domain.challenge.Challenge;
import com.challenge.utils.date.DateUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"challenge_id", "success_date"})
)
public class Record extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate successDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Builder
    private Record(LocalDate successDate, RecordStatus status, Challenge challenge) {
        this.successDate = successDate;
        this.status = status;
        this.challenge = challenge;
        challenge.addRecord(this);
    }

    public static Record create(Challenge challenge, String achieveDate, RecordStatus recordStatus) {
        return Record.builder()
                .challenge(challenge)
                .successDate(DateUtils.toLocalDate(achieveDate))
                .status(recordStatus)
                .build();
    }

}
