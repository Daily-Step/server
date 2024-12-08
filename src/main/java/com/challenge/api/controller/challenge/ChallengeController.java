package com.challenge.api.controller.challenge;

import com.challenge.annotation.AuthMember;
import com.challenge.api.ApiResponse;
import com.challenge.api.controller.challenge.request.ChallengeCreateRequest;
import com.challenge.api.service.challenge.ChallengeService;
import com.challenge.api.service.challenge.response.ChallengeResponse;
import com.challenge.domain.member.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping("/challenges")
    public ApiResponse<List<ChallengeResponse>> getChallenges(@AuthMember Member member) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return ApiResponse.ok(challengeService.getChallenges(member, currentDateTime));
    }

    @PostMapping("/challenges")
    public ApiResponse<ChallengeResponse> createChallenge(@AuthMember Member member,
            @RequestBody @Valid ChallengeCreateRequest request) {
        LocalDateTime startDateTime = LocalDateTime.now();
        return ApiResponse.ok(challengeService.createChallenge(member, request.toServiceRequest(), startDateTime));
    }

    @PostMapping("/challenges/{challengeId}/success")
    public ApiResponse<ChallengeResponse> successChallenge(@PathVariable Long challengeId) {
        LocalDate currentDate = LocalDate.now();
        return ApiResponse.ok(challengeService.successChallenge(challengeId, currentDate));
    }

}
