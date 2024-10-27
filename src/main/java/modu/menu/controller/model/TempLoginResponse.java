package modu.menu.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "로그인(임시) 응답 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TempLoginResponse {

    @Schema(description = "ID", example = "1")
    private Long id;
    @Schema(description = "이름", example = "홍길동")
    private String name;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;
}
