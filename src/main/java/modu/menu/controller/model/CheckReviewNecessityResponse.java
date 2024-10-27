package modu.menu.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modu.menu.service.model.IncompletePlaceServiceResponse;

import java.util.List;

@Schema(description = "리뷰가 필요한 음식점 목록 조회 응답 DTO")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CheckReviewNecessityResponse {

    @Schema(description = "투표율이 동일한 음식점들이 존재할 경우 true, 그렇지 않다면 false를 표시", example = "false")
    private Boolean isSameTurnout;

    @Schema(description = "리뷰가 필요한 음식점 혹은 투표율이 동일한 음식점 목록")
    private List<IncompletePlaceServiceResponse> places;
}
