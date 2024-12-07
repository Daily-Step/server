package com.challenge.api.service.record.response;

import com.challenge.domain.record.Record;
import com.challenge.utils.DateUtils;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RecordResponse {

    private final Long id;
    private final String successDate;

    @Builder
    private RecordResponse(Long id, String successDate) {
        this.id = id;
        this.successDate = successDate;
    }

    public static RecordResponse of(Record record) {
        return RecordResponse.builder()
                .id(record.getId())
                .successDate(DateUtils.toDayString(record.getSuccessDate()))
                .build();
    }

}
