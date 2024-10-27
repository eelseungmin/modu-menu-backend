package modu.menu.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "투표 결과 조회 요청 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoteResultRequest {

    @Schema(description = "회원의 현재 위도", example = "126.977966")
    @Positive(message = "위도는 양수여야 합니다.")
    @NotNull(message = "위도는 필수입니다.")
    private Double latitude;

    @Schema(description = "회원의 현재 경도", example = "37.566535")
    @Positive(message = "경도는 양수여야 합니다.")
    @NotNull(message = "경도는 필수입니다.")
    private Double longitude;
}
