package modu.menu.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modu.menu.domain.VibeType;

import java.util.Arrays;
import java.util.List;

@Schema(description = "분위기 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VibeTypeServiceResponse {

    @Schema(description = "분위기 key")
    private String key;

    @Schema(description = "분위기 value")
    private String value;

    public static List<VibeTypeServiceResponse> toList() {
        return Arrays.stream(VibeType.values())
                .map(vibeType -> VibeTypeServiceResponse.builder()
                        .key(vibeType.name())
                        .value(vibeType.getTitle())
                        .build())
                .toList();
    }
}
