package modu.menu.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "투표 여부 DTO")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IsVoteServiceResponse {

    @Schema(description = "투표자 이름", example = "이승민")
    private String name;

    @Schema(name = "투표 여부", examples = "true")
    private Boolean isVote;
}
