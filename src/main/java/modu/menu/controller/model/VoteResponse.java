package modu.menu.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modu.menu.service.model.VoteResultServiceResponse;

import java.util.List;

@Schema(description = "투표 현황, 결과 조회 응답 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponse {

    @Schema(description = "투표에 포함된 음식점 목록")
    private List<VoteResultServiceResponse> vote;
}
