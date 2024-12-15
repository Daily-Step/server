package com.challenge.api.controller.challenge.request;

import com.challenge.api.service.challenge.request.ChallengeUpdateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeUpdateRequest {

    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(max = 30, message = "제목은 공백 포함 30자 이하여야 합니다.")
    private String title;

    @NotNull(message = "카테고리는 필수 입력값입니다.")
    private Long categoryId;

    @NotBlank(message = "색상은 필수 입력값입니다.")
    private String color;

    @Size(max = 500, message = "상세 내용은 공백 포함 최대 500자 이하여야 합니다.")
    private String content;

    public ChallengeUpdateServiceRequest toServiceRequest() {
        return ChallengeUpdateServiceRequest.builder()
                .title(title)
                .color(color)
                .content(content)
                .build();
    }

}
