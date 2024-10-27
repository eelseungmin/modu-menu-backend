package modu.menu.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modu.menu.domain.FoodType;
import modu.menu.domain.VibeType;

import java.util.List;

@Schema(description = "투표 결과 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoteResultServiceResponse {

    @Schema(description = "음식점 이름", example = "타코벨")
    private String name;

    @Schema(description = "음식 종류", examples = {"양식", "햄버거"})
    private List<FoodType> foods;

    @Schema(description = "음식점 분위기 목록", examples = {"시끌벅적해요", "조용해요"})
    private List<VibeType> vibes;

    @Schema(description = "음식점 도로명 주소", example = "서울 서초구 서초대로74길 11 삼성전자 강남사옥 지하1층 B108호")
    private String address;

    @Schema(description = "음식점과의 거리", example = "10m")
    private String distance;

    @Schema(description = "음식점 관련 이미지 주소")
    private String img;

    @Schema(description = "해당 음식점 득표율(투표가 종료된 경우에만 표시, 그 외엔 null)", examples = {"70%", "null"})
    private String turnout;

    @Schema(description = "조회를 요청한 회원이 해당 장소에 투표했는지 여부", examples = "true")
    private Boolean isVote;
}
