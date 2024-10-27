package modu.menu.controller.model;

import lombok.Builder;
import lombok.Getter;

public class UserResponse {

    @Builder
    @Getter
    public static class LoginResponse {
        private String name;
        private String nickname;
        private String profileImageUrl;
    }

    @Builder
    @Getter
    public static class KakaoToken {
        private String tokenType;
        private String accessToken;
        private String idToken;
        private Integer expiresIn;
        private String refreshToken;
        private Integer refreshTokenExpiresIn;
        private String scope;
    }

    @Builder
    @Getter
    public static class KakaoUserInfo {
        private Long oauthId;
        private String email;
        private String name;
        private String profileImageUrl;
    }
}
