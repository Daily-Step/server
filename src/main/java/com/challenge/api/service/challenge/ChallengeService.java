package com.challenge.api.service.challenge;

import com.challenge.api.service.challenge.request.ChallengeAchieveServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeCancelServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeQueryServiceRequest;
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
import com.challenge.domain.challengeRecord.ChallengeRecord;
import com.challenge.domain.challengeRecord.ChallengeRecordRepository;
import com.challenge.domain.member.Member;
import com.challenge.utils.date.DateUtils;
import com.challenge.validator.DateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeQueryRepository challengeQueryRepository;

    private final ChallengeRepository challengeRepository;
    private final CategoryRepository categoryRepository;
    private final ChallengeRecordRepository challengeRecordRepository;

    private final ChallengeValidator challengeValidator;
    private final CategoryValidator categoryValidator;
    private final RecordValidator recordValidator;

    /**
     * 입력 받은 날짜 기준 이전 2달간의 챌린지 목록 조회 (2달전 00:00:00 ~ 입력받은 날짜 23:59:59)
     */
    public List<ChallengeResponse> getChallenges(Member member, ChallengeQueryServiceRequest request) {
        // validation
        DateValidator.isLocalDateFormatter(request.getQueryDate());
        DateValidator.isBeforeOrEqualToTodayFrom(request.getQueryDate());

        LocalDate targetDate = DateUtils.toLocalDate(request.getQueryDate());
        List<Challenge> challenges = challengeQueryRepository.findChallengesBy(member, targetDate);

        return challenges.stream()
                .map(ChallengeResponse::of)
                .toList();
    }

    @Transactional
    public ChallengeResponse createChallenge(Member member, ChallengeCreateServiceRequest request,
                                             LocalDateTime startDateTime) {
        // validation
        categoryValidator.categoryExistsBy(request.getCategoryId());

        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        Challenge challenge = Challenge.create(member, category, request, startDateTime);
        Challenge savedChallenge = challengeRepository.save(challenge);
        return ChallengeResponse.of(savedChallenge);
    }

    @Transactional
    public ChallengeResponse achieveChallenge(Member member, Long challengeId, ChallengeAchieveServiceRequest request) {
        // validation
        validateChallengeAchieveOrCancel(member, challengeId, request.getAchieveDate());

        Challenge challenge = challengeRepository.getReferenceById(challengeId);
        challengeValidator.hasDuplicateRecordFor(challenge, DateUtils.toLocalDate(request.getAchieveDate()));

        ChallengeRecord challengeRecord = ChallengeRecord.achieve(challenge, request.getAchieveDate());
        challengeRecordRepository.save(challengeRecord);

        return ChallengeResponse.of(challenge);
    }

    @Transactional
    public ChallengeResponse cancelChallenge(Member member, Long challengeId, ChallengeCancelServiceRequest request) {
        // validation
        validateChallengeAchieveOrCancel(member, challengeId, request.getCancelDate());

        Challenge challenge = challengeRepository.getReferenceById(challengeId);

        // validation
        ChallengeRecord challengeRecord = recordValidator.isLatestRecordSuccessfulBy(challenge,
                DateUtils.toLocalDate(request.getCancelDate()));

        // 기록 삭제
        challenge.getChallengeRecords().remove(challengeRecord);
        ChallengeRecord cancelRecord = ChallengeRecord.cancel(challenge, request.getCancelDate());
        challengeRecordRepository.save(cancelRecord);

        return ChallengeResponse.of(challenge);
    }

    @Transactional
    public ChallengeResponse updateChallenge(Member member, Long challengeId, ChallengeUpdateServiceRequest request) {
        // validation
        categoryValidator.categoryExistsBy(request.getCategoryId());
        challengeValidator.challengeExistsBy(member, challengeId);

        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        Challenge challenge = challengeRepository.getReferenceById(challengeId);
        challenge.update(category, request);
        return ChallengeResponse.of(challenge);
    }

    @Transactional
    public Long deleteChallenge(Member member, Long challengeId) {
        // validation
        challengeValidator.challengeExistsBy(member, challengeId);

        Challenge challenge = challengeRepository.getReferenceById(challengeId);
        challenge.delete();
        return challengeId;
    }

    // 챌린지 달성 또는 취소 시 유효성 검사
    private void validateChallengeAchieveOrCancel(Member member, Long challengeId, String actionDate) {
        challengeValidator.challengeExistsBy(member, challengeId);
        DateValidator.isLocalDateFormatter(actionDate);
        DateValidator.isBeforeOrEqualToTodayFrom(actionDate);
    }

}
