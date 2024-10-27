package modu.menu.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TempJoinServiceResponse {

    private Long id;
    private String name;
    private String nickname;
    private String profileImageUrl;
    private String accessToken;
}
