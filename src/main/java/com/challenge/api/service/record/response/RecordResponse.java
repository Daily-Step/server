package com.challenge.api.service.record.response;

import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challengeRecord.ChallengeRecord;
import com.challenge.utils.date.DateUtils;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class RecordResponse {

    private final List<String> successDates;

    @Builder
    private RecordResponse(List<String> successDates) {
        this.successDates = successDates;
    }

    public static RecordResponse of(Challenge challenge) {
        List<ChallengeRecord> challengeRecords = challenge.getChallengeRecords();
        if (challengeRecords.isEmpty()) {
            return null;
        }

        return RecordResponse.builder()
                .successDates(
                        challengeRecords.stream()
                                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                                .filter(ChallengeRecord::isSucceed)
                                .map(record -> DateUtils.toDayString(record.getRecordDate()))
                                .toList()
                )
                .build();
    }

}

