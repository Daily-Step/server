package com.challenge.api.service.record.response;

import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.record.Record;
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
        List<Record> records = challenge.getRecords();
        if (records.isEmpty()) {
            return null;
        }

        return RecordResponse.builder()
                .successDates(
                        records.stream()
                                .map(record -> DateUtils.toDayString(record.getDate()))
                                .toList()
                )
                .build();
    }

}

