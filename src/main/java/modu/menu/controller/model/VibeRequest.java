package modu.menu.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modu.menu.core.annotation.EnumValidation;
import modu.menu.domain.VibeType;

@Schema(description = "분위기 요청 DTO")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VibeRequest {

    @Schema(description = "분위기", example = "NOISY", allowableValues = {"NOISY", "TRENDY", "GOOD_SERVICE", "QUIET", "MODERN", "NICE_VIEW"})
    @NotNull(message = "vibe는 필수입니다.")
    @EnumValidation(enumClass = VibeType.class, message = "허용되는 값만 입력해주세요.")
    private String type;
}
