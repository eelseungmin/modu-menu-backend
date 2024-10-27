package modu.menu.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "투표 생성 요청 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SaveVoteRequest {

    @Schema(description = "투표에 포함시킬 음식점 ID 목록")
    @Size(min = 2, max = 3, message = "투표 항목으로 최소 2개, 최대 3개의 음식점이 포함되어야 합니다.")
    private List<@Positive(message = "ID는 양수여야 합니다.") Long> placeIds;
}
