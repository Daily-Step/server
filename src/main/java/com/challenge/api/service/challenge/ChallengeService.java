package com.challenge.api.service.challenge;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeUpdateServiceRequest;
import com.challenge.api.service.challenge.response.ChallengeResponse;
import com.challenge.api.validator.CategoryValidator;
import com.challenge.api.validator.ChallengeValidator;
import com.challenge.api.validator.RecordValidator;
import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challenge.ChallengeQueryRepository;
import com.challenge.domain.challenge.ChallengeRepository;
import com.challenge.domain.member.Member;
import com.challenge.domain.record.Record;
import com.challenge.domain.record.RecordRepository;
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
    private final RecordValidator recordValidator;

    public List<ChallengeResponse> getChallenges(Member member, LocalDateTime currentDateTime) {
        List<Challenge> challenges = challengeQueryRepository.findChallengesBy(member, currentDateTime);

        return challenges.stream()
                .map(ChallengeResponse::of)
                .toList();
    }

    @Transactional
    public ChallengeResponse createChallenge(Member member, ChallengeCreateServiceRequest request,
            LocalDateTime startDateTime) {
        categoryValidator.categoryExistsBy(request.getCategoryId());

        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        Challenge challenge = Challenge.create(member, category, request, startDateTime);
        Challenge savedChallenge = challengeRepository.save(challenge);
        return ChallengeResponse.of(savedChallenge);
    }

    @Transactional
    public ChallengeResponse achieveChallenge(Member member, Long challengeId, String achieveDate) {
        challengeValidator.challengeExistsBy(member, challengeId);
        DateValidator.isLocalDateFormatter(achieveDate);
        DateValidator.isBeforeOrEqualToTodayFrom(achieveDate);

        Challenge challenge = challengeRepository.getReferenceById(challengeId);
        challengeValidator.hasDuplicateRecordFor(challenge, DateUtils.toLocalDate(achieveDate));

        Record record = Record.achieve(challenge, achieveDate);
        recordRepository.save(record);
        challenge.addRecord(record);

        return ChallengeResponse.of(challenge);
    }

    @Transactional
    public ChallengeResponse cancelChallenge(Member member, Long challengeId, String cancelDate) {
        challengeValidator.challengeExistsBy(member, challengeId);
        DateValidator.isLocalDateFormatter(cancelDate);
        DateValidator.isBeforeOrEqualToTodayFrom(cancelDate);

        Challenge challenge = challengeRepository.getReferenceById(challengeId);

        Record record = recordValidator.hasRecordFor(challenge, DateUtils.toLocalDate(cancelDate));
        challenge.getRecords().remove(record);

        return ChallengeResponse.of(challenge);
    }

    @Transactional
    public ChallengeResponse updateChallenge(Member member, Long challengeId, ChallengeUpdateServiceRequest request) {
        categoryValidator.categoryExistsBy(request.getCategoryId());
        challengeValidator.challengeExistsBy(member, challengeId);

        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        Challenge challenge = challengeRepository.getReferenceById(challengeId);
        challenge.update(category, request);
        return ChallengeResponse.of(challenge);
    }

    @Transactional
    public Void deleteChallenge(Member member, Long challengeId) {
        challengeValidator.challengeExistsBy(member, challengeId);
        Challenge challenge = challengeRepository.getReferenceById(challengeId);
        challengeRepository.delete(challenge);
        return null;
    }

}
