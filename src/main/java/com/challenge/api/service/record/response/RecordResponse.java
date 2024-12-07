package com.challenge.api.service.record.response;

import com.challenge.domain.record.Record;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RecordResponse {

    private final Long id;
    private final LocalDate successDate;

    @Builder
    private RecordResponse(Long id, LocalDate successDate) {
        this.id = id;
        this.successDate = successDate;
    }

    public static RecordResponse of(Record record) {
        return RecordResponse.builder()
                .id(record.getId())
                .successDate(record.getSuccessDate())
                .build();
    }

}
