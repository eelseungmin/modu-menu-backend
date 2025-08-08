package modu.menu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import modu.menu.core.annotation.EnumValidation;
import modu.menu.core.response.ApiFailResponse;
import modu.menu.core.response.ApiSuccessResponse;
import modu.menu.domain.FoodType;
import modu.menu.controller.model.CategoryResponse;
import modu.menu.controller.model.SearchPlaceResponse;
import modu.menu.service.PlaceService;
import modu.menu.domain.VibeType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "장소")
@Validated
@RequiredArgsConstructor
@RestController
public class PlaceController {

    private final PlaceService placeService;

    @Operation(summary = "카테고리 목록 조회", description = """
            음식 카테고리와 분위기의 전체 목록을 조회합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회가 성공한 경우"),
            @ApiResponse(responseCode = "500", description = "그 외 서버에서 처리하지 못한 에러가 발생했을 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class)))
    })
    @GetMapping("/api/category")
    public ResponseEntity<ApiSuccessResponse<CategoryResponse>> getCategory() {

        return ResponseEntity.ok()
                .body(new ApiSuccessResponse<>(placeService.getCategory()));
    }

    @Operation(summary = "음식점 후보 검색", description = """
            투표에 포함시킬 음식점 후보를 사용자의 입력 값을 바탕으로 검색합니다. 조건을 만족하는 음식점이 없을 경우 null을 반환합니다.\n
            Query String의 경우 대소문자를 구분하지 않습니다.\n""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색이 성공한 경우"),
            @ApiResponse(responseCode = "400", description = "Query String이 형식에 맞지 않을 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class))),
            @ApiResponse(responseCode = "500", description = "그 외 서버에서 처리하지 못한 에러가 발생했을 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class)))
    })
    @GetMapping("/api/place")
    public ResponseEntity<ApiSuccessResponse<SearchPlaceResponse>> searchPlace(
            @PositiveOrZero(message = "위도는 0 또는 양수여야 합니다.") @RequestParam(defaultValue = "37.505098") Double latitude,
            @PositiveOrZero(message = "경도는 0 또는 양수여야 합니다.") @RequestParam(defaultValue = "127.032941") Double longitude,
            @EnumValidation(enumClass = FoodType.class, message = "음식에 허용된 값만 입력해주세요.") @Size(min = 1, message = "음식은 입력 시 최소 1개의 값이 필요합니다.")
            @RequestParam(required = false) List<String> food,
            @EnumValidation(enumClass = VibeType.class, message = "분위기에 허용된 값만 입력해주세요.") @Size(min = 1, message = "분위기는 입력 시 최소 1개의 값이 필요합니다.")
            @RequestParam(required = false) List<String> vibe,
            @PositiveOrZero(message = "페이지 번호는 0 또는 양수여야 합니다.") @RequestParam(defaultValue = "0") Integer page
    ) {
        SearchPlaceResponse searchPlaceResponse = placeService.searchPlace(
                latitude,
                longitude,
                food != null ? food.stream().map(f -> FoodType.valueOf(f.toUpperCase())).toList() : null,
                vibe != null ? vibe.stream().map(v -> VibeType.valueOf(v.toUpperCase())).toList() : null,
                page
        );

        return ResponseEntity.ok()
                .body(new ApiSuccessResponse<>(searchPlaceResponse));
    }

    @PostMapping("/api/places")
    public ResponseEntity<ApiSuccessResponse> insertDummyData() {
        placeService.insertDummyData();

        return ResponseEntity.ok()
                .body(new ApiSuccessResponse());
    }
}