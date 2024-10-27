package modu.menu.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modu.menu.domain.FoodType;

import java.util.Arrays;
import java.util.List;

@Schema(description = "음식점 카테고리 DTO")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FoodTypeServiceResponse {

    @Schema(description = "음식점 카테고리 key")
    private String key;

    @Schema(description = "음식점 카테고리 value")
    private String value;

    @Schema(description = "하위 카테고리 목록")
    private List<FoodTypeServiceResponse> children;

    // 최상위 카테고리부터 시작하여 FoodType의 전체 계층 구조를 반환하는 메서드
    public static List<FoodTypeServiceResponse> getFoodTypeHierarchy() {
        return FoodType.getAncestor().stream()
                .map(FoodTypeServiceResponse::toFoodTypeServiceResponse)
                .toList();
    }

    private static FoodTypeServiceResponse toFoodTypeServiceResponse(FoodType foodType) {
        return FoodTypeServiceResponse.builder()
                .key(foodType.name())
                .value(foodType.getTitle())
                .children(Arrays.stream(FoodType.values())
                        .filter(f -> f.getParent() == foodType)
                        .map(FoodTypeServiceResponse::toFoodTypeServiceResponse)
                        .toList())
                .build();
    }
}
