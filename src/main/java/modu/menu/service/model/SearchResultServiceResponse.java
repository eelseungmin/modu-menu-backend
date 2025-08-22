package modu.menu.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modu.menu.domain.FoodType;
import modu.menu.domain.VibeType;

import java.util.List;

@Schema(description = "검색 결과 DTO")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultServiceResponse {

    @Schema(description = "음식점 ID", example = "1")
    private Long id;

    @Schema(description = "음식점 이름", example = "타코벨")
    private String name;

    @Schema(description = "검색에 사용한 음식 조건 목록", examples = {
            "면",
            "양식"})
    private List<FoodType> foods;

    @Schema(description = "검색에 사용한 분위기 조건 목록", examples = {
            "시끌벅적해요",
            "트렌디해요",
            "조용해요"})
    private List<VibeType> vibes;

    @Schema(description = "음식점 도로명 주소", example = "서울 서초구 서초대로74길 11 삼성전자 강남사옥 지하1층 B108호")
    private String address;

    @Schema(description = "음식점과의 거리", example = "10m")
    private String distance;

    @Schema(description = "음식점 관련 이미지 주소")
    private String imageUrl;

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
