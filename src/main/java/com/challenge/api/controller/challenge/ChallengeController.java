package com.challenge.api.controller.challenge;

import com.challenge.annotation.AuthMember;
import com.challenge.api.ApiResponse;
import com.challenge.api.controller.challenge.request.ChallengeCreateRequest;
import com.challenge.api.service.challenge.ChallengeService;
import com.challenge.api.service.challenge.response.ChallengeResponse;
import com.challenge.domain.member.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping("/challenges")
    public ApiResponse<ChallengeResponse> createChallenge(@RequestBody @Valid ChallengeCreateRequest request,
            @AuthMember Member member) {
        Long memberId = member.getId();
        LocalDateTime startDateTime = LocalDateTime.now();
        return ApiResponse.ok(challengeService.createChallenge(memberId, request.toServiceRequest(), startDateTime));
    }

    @GetMapping("/challenges")
    public ApiResponse<List<Map<String, Object>>> getChallenges(@AuthMember Member member) {
        Long memberId = member.getId();
        return ApiResponse.ok(challengeService.getChallenges(memberId));
    }


}
