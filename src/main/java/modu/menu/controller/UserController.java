package modu.menu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import modu.menu.core.auth.jwt.JwtProvider;
import modu.menu.core.response.ApiFailResponse;
import modu.menu.core.response.ApiSuccessResponse;
import modu.menu.controller.model.TempJoinRequest;
import modu.menu.controller.model.TempLoginRequest;
import modu.menu.controller.model.TempJoinResponse;
import modu.menu.controller.model.TempLoginResponse;
import modu.menu.service.UserService;
import modu.menu.service.model.TempJoinServiceResponse;
import modu.menu.service.model.TempLoginServiceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "회원가입(임시)", description = "회원 정보를 이용해 가입합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입이 성공한 경우"),
            @ApiResponse(responseCode = "400", description = "TempJoinRequest의 값이 형식에 맞지 않거나, 이미 가입된 이메일의 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class))),
            @ApiResponse(responseCode = "500", description = "그 외 서버에서 처리하지 못한 에러가 발생했을 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class)))
    })
    @PostMapping("/api/user")
    public ResponseEntity<ApiSuccessResponse<TempJoinResponse>> tempJoin(
            @Valid @RequestBody TempJoinRequest tempJoinRequest
    ) {

        TempJoinServiceResponse response = userService.tempJoin(tempJoinRequest);

        return ResponseEntity.ok()
                .header(jwtProvider.ACCESS_HEADER, response.getAccessToken())
                .body(
                        new ApiSuccessResponse<>(TempJoinResponse.builder()
                                .id(response.getId())
                                .name(response.getName())
                                .nickname(response.getNickname())
                                .profileImageUrl(response.getProfileImageUrl())
                                .build())
                );
    }

    @Operation(summary = "로그인(임시)", description = "이메일과 비밀번호를 이용해 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입이 성공한 경우"),
            @ApiResponse(responseCode = "400", description = "TempLoginRequest의 값이 형식에 맞지 않는 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class))),
            @ApiResponse(responseCode = "401", description = "입력한 이메일 또는 비밀번호가 잘못된 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class))),
            @ApiResponse(responseCode = "500", description = "그 외 서버에서 처리하지 못한 에러가 발생했을 경우", content = @Content(schema = @Schema(implementation = ApiFailResponse.class)))
    })
    @PostMapping("/api/user/login")
    public ResponseEntity<ApiSuccessResponse<TempLoginResponse>> tempLogin(
            @Valid @RequestBody TempLoginRequest tempLoginRequest
    ) {

        TempLoginServiceResponse response = userService.tempLogin(tempLoginRequest);

        return ResponseEntity.ok()
                .header(jwtProvider.ACCESS_HEADER, response.getAccessToken())
                .body(
                        new ApiSuccessResponse<>(TempLoginResponse.builder()
                                .id(response.getId())
                                .name(response.getName())
                                .nickname(response.getNickname())
                                .profileImageUrl(response.getProfileImageUrl())
                                .build())
                );
    }

//    @GetMapping("/login/oauth/{provider}")
//    public ResponseEntity<ApiResponse> login(
//            @PathVariable String provider,
//            String code
//    ) {
//        if (!provider.equals("kakao")) {
//            throw new Exception400("provider", ErrorMessage.LOGIN_USER_PROVIDER_ERROR);
//        }
//        User user = userService.login(provider, code);
//        Pair<String, String> tokens = userService.issue(user);
//
//        return ResponseEntity.ok()
//                .header(JwtProperties.HEADER_ACCESS, tokens.getFirst())
//                .header(JwtProperties.HEADER_REFRESH, tokens.getSecond())
//                .body(new ApiResponse(UserResponse.LoginResponse.builder()
//                        .name(user.getName())
//                        .nickname(user.getNickname())
//                        .profileImageUrl(user.getProfileImageUrl())
//                        .build())
//                );
//    }
}
