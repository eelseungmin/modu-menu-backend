package modu.menu.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modu.menu.domain.FoodType;

import java.util.List;

@Schema(description = "리뷰가 필요한 음식점 목록 응답 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IncompletePlaceServiceResponse {

    @Schema(description = "음식점 ID", example = "1")
    private Long id;

    @Schema(description = "음식점 이름", example = "타코벨")
    private String name;

    @Schema(description = "음식 종류", examples = {"양식", "햄버거"})
    private List<FoodType> foods;

    @Schema(description = "음식점 도로명 주소", example = "서울 서초구 서초대로74길 11 삼성전자 강남사옥 지하1층 B108호")
    private String address;

    @Schema(description = "음식점 관련 이미지 주소")
    private String img;
}
