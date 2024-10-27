package modu.menu.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modu.menu.core.annotation.EnumValidation;
import modu.menu.domain.HasRoom;

import java.util.List;

@Schema(description = "리뷰 등록 요청 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequest {

    @Schema(description = "평점")
    @NotNull(message = "rating은 필수입니다.")
    @Min(value = 1, message = "최소 평점은 1입니다.")
    @Max(value = 5, message = "최대 평점은 5입니다.")
    private Integer rating;

    @Schema(description = "음식점 분위기 목록")
    @Size(min = 1, message = "vibes는 하나 이상의 값이 필요합니다.")
    @Valid
    private List<VibeRequest> vibes;

    @Schema(description = "몇 명과 함께 갔나요?")
    @PositiveOrZero(message = "participants는 0 이상의 양수여야 합니다.")
    private Integer participants;

    @Schema(description = "룸이 있었나요?", example = "YES", allowableValues = {"YES", "NO", "UNKNOWN"})
    @EnumValidation(
            enumClass = HasRoom.class,
            message = "'YES', 'NO, 'UNKNOWN'만 입력해주세요."
    )
    private String hasRoom;

    @Schema(description = "리뷰", example = "양 많고 사장님이 친절합니다.")
    @Size(max = 100, message = "100자 이내로 입력해주세요.")
    private String content;
}
