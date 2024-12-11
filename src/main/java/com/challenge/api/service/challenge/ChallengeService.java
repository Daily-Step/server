package com.challenge.api.service.challenge;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.api.service.challenge.response.ChallengeResponse;
import com.challenge.api.validator.CategoryValidator;
import com.challenge.api.validator.ChallengeValidator;
import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challenge.ChallengeQueryRepository;
import com.challenge.domain.challenge.ChallengeRepository;
import com.challenge.domain.member.Member;
import com.challenge.domain.record.Record;
import com.challenge.domain.record.RecordRepository;
import com.challenge.domain.record.RecordStatus;
import com.challenge.utils.date.DateUtils;
import com.challenge.validator.DateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeQueryRepository challengeQueryRepository;

    private final ChallengeRepository challengeRepository;
    private final CategoryRepository categoryRepository;
    private final RecordRepository recordRepository;

    private final ChallengeValidator challengeValidator;
    private final CategoryValidator categoryValidator;

    public List<ChallengeResponse> getChallenges(Member member, LocalDateTime currentDateTime) {
        List<Challenge> challenges = challengeQueryRepository.findChallengesBy(member, currentDateTime);

        return challenges.stream()
                .map(ChallengeResponse::of)
                .toList();
    }

    @Transactional
    public ChallengeResponse createChallenge(Member member, ChallengeCreateServiceRequest request,
            LocalDateTime startDateTime) {
        categoryValidator.validateCategoryExists(request.getCategoryId());

        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        Challenge challenge = Challenge.create(member, category, request, startDateTime);
        Challenge savedChallenge = challengeRepository.save(challenge);
        return ChallengeResponse.of(savedChallenge);
    }

    @Transactional
    public ChallengeResponse achieveChallenge(Member member, Long challengeId, String achieveDate) {
        DateValidator.isLocalDateFormatter(achieveDate);
        DateValidator.isBeforeOrEqualToTodayFrom(achieveDate);
        challengeValidator.challengeExists(member, challengeId);

        Challenge challenge = challengeRepository.getReferenceById(challengeId);

        challengeValidator.duplicateRecordBy(challenge, DateUtils.toLocalDate(achieveDate));

        Record record = Record.create(challenge, achieveDate, RecordStatus.ACHIEVEMENT_COMPLETED);
        Record savedRecord = recordRepository.save(record);
        challenge.addRecord(savedRecord);

        Challenge savedChallenge = challengeRepository.save(challenge);
        return ChallengeResponse.of(savedChallenge);
    }

}
