package modu.menu.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modu.menu.service.model.FoodTypeServiceResponse;
import modu.menu.service.model.VibeTypeServiceResponse;

import java.util.List;

@Schema(description = "카테고리 조회 결과 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    @Schema(description = "음식점 카테고리 목록")
    private List<FoodTypeServiceResponse> foods;

    @Schema(description = "분위기 목록")
    private List<VibeTypeServiceResponse> vibes;
}
