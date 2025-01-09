package com.challenge.api.validator;

import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.record.Record;
import com.challenge.domain.record.RecordRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecordValidator {

    private final RecordRepository recordRepository;

    /**
     * 특정 챌린지와 날짜에 해당하는 레코드가 존재하는지 검증하고 반환
     *
     * @param challenge  검증 대상 챌린지
     * @param cancelDate 취소하려는 날짜
     * @return 검증된 Record 엔티티
     * @throws GlobalException RECORD_NOT_FOUND 예외
     */
    public Record hasRecordFor(Challenge challenge, LocalDate cancelDate) {
        return challenge.getRecords().stream()
                .filter(r -> r.getDate().equals(cancelDate))
                .findFirst()
                .orElseThrow(() -> new GlobalException(ErrorCode.RECORD_NOT_FOUND));
    }

}
