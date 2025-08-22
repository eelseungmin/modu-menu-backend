package modu.menu.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modu.menu.service.model.SearchResultServiceResponse;

import java.util.List;

@Schema(description = "음식점 후보 검색 결과 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchPlaceResponse {

    @Schema(description = "검색 결과 목록")
    private List<SearchResultServiceResponse> results;

    @Schema(description = "페이지 내 목록 갯수")
    private Integer totalElements;

    @Schema(description = "총 페이지 수")
    private Integer totalPages;

    @Schema(description = "현재 페이지 번호")
    private Integer currentPageNumber;

    @Schema(description = "현재 페이지가 첫 번째 페이지인지 여부")
    private Boolean isFirst;

    @Schema(description = "현재 페이지가 마지막 페이지인지 여부")
    private Boolean isLast;

    @Schema(description = "보여줄 페이지가 하나도 없는지 여부")
    private Boolean isEmpty;
}