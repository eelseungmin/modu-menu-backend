package modu.menu.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "투표 요청 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequest {

    @Schema(description = "투표하려는 음식점 ID")
    @Positive(message = "placeId는 양수여야 합니다.")
    private Long placeId;
}
