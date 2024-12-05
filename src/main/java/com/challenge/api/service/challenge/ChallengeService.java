package com.challenge.api.service.challenge;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.api.service.challenge.response.ChallengeResponse;
import com.challenge.api.validator.CategoryValidator;
import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challenge.ChallengeRepository;
import com.challenge.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final CategoryRepository categoryRepository;

    private final CategoryValidator categoryValidator;

    @Transactional
    public ChallengeResponse createChallenge(Member member, ChallengeCreateServiceRequest request,
            LocalDateTime startDateTime) {
        categoryValidator.validateCategoryExists(request.getCategoryId());

        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        Challenge challenge = Challenge.create(member, category, request, startDateTime);
        Challenge savedChallenge = challengeRepository.save(challenge);
        return ChallengeResponse.of(savedChallenge);
    }

    public List<ChallengeResponse> getChallenges(Member member, LocalDateTime currentDateTime) {
        List<Challenge> challenges = challengeRepository.findChallengesBy(member, currentDateTime);

        return challenges.stream()
                .map(ChallengeResponse::of)
                .toList();
    }

}
