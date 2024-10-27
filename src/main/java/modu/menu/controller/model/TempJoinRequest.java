package modu.menu.controller.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "회원가입(임시) 요청 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TempJoinRequest {

    @Schema(description = "이메일", example = "hangang416@naver.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식을 지켜주세요.")
    private String email;

    @Schema(description = "비밀번호")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(description = "이름")
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Schema(description = "닉네임")
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @Schema(description = "성별", example = "M", allowableValues = {"M", "W"})
    @Pattern(
            regexp = "^[MW]$",
            message = "성별 값은 'M' 또는 'W'만 입력해주세요."
    )
    @NotBlank(message = "성별은 필수입니다.")
    private String gender;

    @Schema(description = "연령", example = "25")
    @NotNull(message = "연령은 필수입니다.")
    @PositiveOrZero
    private Integer age;

    @Schema(description = "생일", example = "1997-01-01")
    @NotNull(message = "생일은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @Schema(description = "휴대폰 번호", example = "01012345678")
    @Pattern(
            regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
            message = "유효하지 않은 휴대폰 번호 형식입니다."
    )
    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    private String phoneNumber;

}
