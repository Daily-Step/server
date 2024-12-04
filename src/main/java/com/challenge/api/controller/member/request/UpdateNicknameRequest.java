package com.challenge.api.controller.member.request;

import com.challenge.api.service.member.request.UpdateNicknameServiceRequest;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNicknameRequest {

    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{4,10}$", message = "닉네임은 4~10자이며, 띄어쓰기와 특수문자를 사용할 수 없습니다.")
    private String nickname;

    public UpdateNicknameServiceRequest toServiceRequest() {
        return UpdateNicknameServiceRequest.builder()
                .nickname(nickname)
                .build();
    }

    @Builder
    private UpdateNicknameRequest(String nickname) {
        this.nickname = nickname;
    }

}
