package modu.menu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import modu.menu.core.exception.Exception401;
import modu.menu.core.response.ApiFailResponse;
import modu.menu.core.response.ApiSuccessResponse;
import modu.menu.core.response.ErrorMessage;
import modu.menu.controller.model.CreateReviewRequest;
import modu.menu.controller.model.CheckReviewNecessityResponse;
import modu.menu.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리뷰")
@Validated
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰가 필요한 음식점 목록 조회", description = """
            회원이 아직 평가하지 않은 음식점이 있다면 관련 음식점에 대한 정보를 반환합니다. 그렇지 않다면 null을 반환합니다.\n\n
            평가하지 않았지만 득표율이 동일한 경우 해당하는 음식점 목록을 반환합니다.\n\n
            isSameTurnout은 득표율이 동일한지 여부를 나타냅니다.""")
    @SecurityRequirement(name = "Authorization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "평가하지 않은 투표가 있거나 그렇지 않은 경우, 또는 평가하지는 않았지만 투표율이 동일한 음식점 목록을 반환할 경우"),
            @ApiResponse(responseCode = "401", description = "토큰 인증이 실패한 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class))),
            @ApiResponse(responseCode = "500", description = "그 외 서버에서 처리하지 못한 에러가 발생했을 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class)))
    })
    @GetMapping("/api/user/{userId}/incomplete-place")
    public ResponseEntity<ApiSuccessResponse<CheckReviewNecessityResponse>> checkReviewNecessity(
            @Positive(message = "userId는 양수여야 합니다.") @PathVariable("userId") Long userId,
            @RequestAttribute("userId") Long tokenUserId
    ) {
        if (!userId.equals(tokenUserId)) {
            throw new Exception401(ErrorMessage.CANT_MATCH_TOKEN_WITH_PATH_VARIABLE);
        }

        CheckReviewNecessityResponse checkReviewNecessityResponse = reviewService.checkReviewNecessity(userId);

        return ResponseEntity.ok()
                .body(new ApiSuccessResponse<>(checkReviewNecessityResponse));
    }

    @Operation(summary = "리뷰 등록", description = "투표로 결정된 음식점에 대한 리뷰를 등록합니다. 반드시 리뷰가 필요한 음식점 목록 조회 API가 먼저 호출되어야 합니다.")
    @SecurityRequirement(name = "Authorization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록이 성공한 경우"),
            @ApiResponse(responseCode = "400", description = "CreateReviewRequest 또는 PathVariable이 형식에 맞지 않을 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class))),
            @ApiResponse(responseCode = "401", description = "토큰 인증이 실패한 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class))),
            @ApiResponse(responseCode = "404", description = "조회하려는 투표 자체가 존재하지 않는 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class))),
            @ApiResponse(responseCode = "500", description = "그 외 서버에서 처리하지 못한 에러가 발생했을 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class)))
    })
    @PostMapping("/api/place/{placeId}/review")
    public ResponseEntity<ApiSuccessResponse> createReview(
            @Positive(message = "placeId는 양수여야 합니다.") @PathVariable("placeId") Long placeId,
            @Valid @RequestBody CreateReviewRequest createReviewRequest,
            @RequestAttribute("userId") Long userId
    ) {
        reviewService.createReview(userId, placeId, createReviewRequest);

        return ResponseEntity.ok()
                .body(new ApiSuccessResponse<>());
    }
}
