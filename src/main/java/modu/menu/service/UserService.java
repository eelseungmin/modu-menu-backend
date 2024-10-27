package modu.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import modu.menu.core.auth.jwt.JwtProvider;
import modu.menu.core.auth.oauth2.InMemoryProviderRepository;
import modu.menu.core.exception.Exception400;
import modu.menu.core.exception.Exception401;
import modu.menu.core.response.ErrorMessage;
import modu.menu.controller.model.TempJoinRequest;
import modu.menu.controller.model.TempLoginRequest;
import modu.menu.domain.Gender;
import modu.menu.domain.User;
import modu.menu.domain.UserStatus;
import modu.menu.repository.UserRepository;
import modu.menu.service.model.TempJoinServiceResponse;
import modu.menu.service.model.TempLoginServiceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final InMemoryProviderRepository inMemoryProviderRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public TempJoinServiceResponse tempJoin(TempJoinRequest tempJoinRequest) {
        userRepository.findByEmail(tempJoinRequest.getEmail()).ifPresent(user -> {
                    throw new Exception400("email", ErrorMessage.DUPLICATE_EMAIL.getValue());
        });

        User user = userRepository.save(
                User.builder()
                        .email(tempJoinRequest.getEmail())
                        .password(tempJoinRequest.getPassword())
                        .name(tempJoinRequest.getName())
                        .nickname(tempJoinRequest.getNickname())
                        .gender(tempJoinRequest.getGender().equals("M") ? Gender.MALE : Gender.FEMALE)
                        .age(tempJoinRequest.getAge())
                        .birthday(tempJoinRequest.getBirthdate())
                        .phoneNumber(tempJoinRequest.getPhoneNumber())
                        .status(UserStatus.ACTIVE)
                        .build()
        );

        return TempJoinServiceResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .accessToken(jwtProvider.createAccessToken(user.getId()))
                .build();
    }

    public TempLoginServiceResponse tempLogin(TempLoginRequest tempLoginRequest) {
        User user = userRepository.findByEmail(tempLoginRequest.getEmail()).orElseThrow(
                () -> new Exception401(ErrorMessage.LOGIN_USER_WRONG_EMAIL)
        );

        if(!user.getPassword().equals(tempLoginRequest.getPassword())) {
            throw new Exception401(ErrorMessage.LOGIN_USER_WRONG_PASSWORD);
        }

        return TempLoginServiceResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .accessToken(jwtProvider.createAccessToken(user.getId()))
                .build();
    }

//    @Transactional
//    public void join(User user) {
//        try {
//            userRepository.save(user);
//        } catch (Exception e) {
//            throw new Exception500(ErrorMessage.ADD_USER_DB_ERROR);
//        }
//    }
//
//    @Transactional
//    public User login(String providerName, String code) {
//        // provider(kakao, naver 등)에 따라 알맞은 OauthProvider를 가져온다.
//        OauthProvider provider = inMemoryProviderRepository.findByProviderName(providerName);
//
//        /**
//         * 카카오 로그인
//         * 1. 클라이언트가 전달한 인가 코드를 이용해 카카오 토큰(액세스 토큰, 리프레쉬 토큰 포함)을 가져온다.
//         * 2. 받아온 액세스 토큰을 이용해 카카오 회원 정보를 가져온다.
//         * 3. 받아온 회원 정보를 이용해서 db에 존재하는 회원인지 판별한다.
//         * 3-1. 존재한다면 로그인 처리(서비스 서버의 액세스 토큰과 리프레쉬 토큰을 발급)를 한다.
//         * 3-2. 존재하지 않는다면 회원 정보를 이용해 회원가입 처리(db에 회원 정보 저장)를 진행한다. 이어서 로그인 처리까지 완료한다.
//         * 4. 발급된 서비스 서버의 토큰 및 기타 정보를 클라이언트에 응답한다.
//         */
//
//        // 1. 클라이언트가 전달한 인가 코드를 이용해 카카오 토큰(액세스 토큰, 리프레쉬 토큰 포함)을 가져온다.
//        UserResponse.KakaoToken kakaoToken = getKakaoToken(provider, code);
//
//        // 2. 받아온 액세스 토큰을 이용해 카카오 회원 정보를 가져온다.
//        UserResponse.KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(kakaoToken.getAccessToken());
//
//        // 3. 받아온 회원 정보를 이용해서 db에 존재하는 회원인지 판별한다.
//        Optional<Oauth> oauthOp = oauthRepository.findByOauthId(kakaoUserInfo.getOauthId());
//        // 3-1. 존재한다면 로그인 처리(서비스 서버의 액세스 토큰과 리프레쉬 토큰을 발급)를 한다.
//        // 3-2. 존재하지 않는다면 회원 정보를 이용해 회원가입 처리(db에 회원 정보 저장)를 진행한다. 이어서 로그인 처리까지 완료한다.
//        User user;
//        Oauth oauth;
//        if (oauthOp.isPresent()) {
//            oauth = oauthOp.get();
//            oauth.update(kakaoToken);
//            user = userRepository.findById(oauth.getUser().getId()).get();
//        } else {
//            user = userRepository.save(
//                    User.builder()
//                            .email(kakaoUserInfo.getEmail())
//                            .name(kakaoUserInfo.getName())
//                            .profileImageUrl(kakaoUserInfo.getProfileImageUrl())
//                            .build()
//            );
//            oauth = oauthRepository.save(
//                    Oauth.builder()
//                            .user(user)
//                            .oauthId(kakaoUserInfo.getOauthId())
//                            .provider(Provider.KAKAO)
//                            .accessToken(kakaoToken.getAccessToken())
//                            .refreshToken(kakaoToken.getRefreshToken())
//                            .build()
//            );
//        }
//
//        // 4. 필요한 정보를 응답한다.
//        return user;
//    }
//
//    // 서비스 서버용 액세스 토큰, 리프레쉬 토큰 발급하기
//    public Pair<String, String> issue(User user) {
//        return Pair.of(JwtProvider.createAccessToken(user), JwtProvider.createRefreshToken(user));
//    }
//
//    // 카카오 토큰 받기
//    // Ref: https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token
//    private UserResponse.KakaoToken getKakaoToken(OauthProvider provider, String code) {
//        RestTemplate template = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        JSONObject requestBody = null;
//        try {
//            requestBody = new JSONObject();
//            requestBody.put("grant_type", "authorization_code");
//            requestBody.put("client_id", provider.getClientId());
//            requestBody.put("redirect_uri", provider.getRedirectUrl());
//            requestBody.put("code", code);
//            requestBody.put("client_secret", provider.getClientSecret());
//        } catch (Exception e) {
//            throw new Exception500(ErrorMessage.ADD_REQUEST_BODY_ERROR);
//        }
//
//        // 카카오 토큰 발급 요청, 실패시 최대 3번까지 재시도
//        int maxRetry = 3;
//        String responseBody = "";
//        while (maxRetry > 0) {
//            ResponseEntity<String> response = template.exchange(
//                    "https://kauth.kakao.com/oauth/token",
//                    HttpMethod.POST,
//                    new HttpEntity<>(requestBody.toString(), headers),
//                    String.class
//            );
//            // 성공
//            if (response.getStatusCode() == HttpStatus.OK) {
//                responseBody = response.getBody();
//                break;
//            }
//            // 실패
//            maxRetry--;
//            log.warn("...카카오 토큰 가져오기에 실패했습니다. 남은 재시도 횟수: {}", maxRetry);
//        }
//        if (maxRetry <= 0) {
//            throw new Exception500(ErrorMessage.GET_KAKAO_TOKEN_ERROR);
//        }
//
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode node = null;
//        try {
//            node = mapper.readTree(responseBody);
//        } catch (Exception e) {
//            throw new Exception500(ErrorMessage.MAPPER_READ_TREE_ERROR);
//        }
//
//        return UserResponse.KakaoToken.builder()
//                .tokenType(node.path("token_type").asText())
//                .accessToken(node.path("access_token").asText())
//                .idToken(node.path("id_token").asText())
//                .expiresIn(node.path("expires_in").asInt())
//                .refreshToken(node.path("refresh_token").asText())
//                .refreshTokenExpiresIn(node.path("refresh_token_expires_in").asInt())
//                .scope(node.path("scope").asText())
//                .build();
//    }
//
//    // 카카오 사용자 정보 가져오기
//    // Ref: https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
//    private UserResponse.KakaoUserInfo getKakaoUserInfo(String accessToken) {
//        RestTemplate template = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.add("Authorization", JwtProperties.TOKEN_PREFIX + accessToken);
//
//        Map<String, Boolean> params = new HashMap<>();
//        params.put("secure_resource", true);
//
//        // 카카오 사용자 정보 요청, 실패시 최대 3번까지 재시도
//        int maxRetry = 3;
//        String responseBody = "";
//        while (maxRetry > 0) {
//            ResponseEntity<String> response = template.exchange(
//                    "https://kapi.kakao.com/v2/user/me",
//                    HttpMethod.GET,
//                    new HttpEntity<>(headers),
//                    String.class,
//                    params
//            );
//            // 성공
//            if (response.getStatusCode() == HttpStatus.OK) {
//                responseBody = response.getBody();
//                break;
//            }
//            // 실패
//            maxRetry--;
//            log.warn("...카카오 사용자 정보 가져오기에 실패했습니다. 남은 재시도 횟수: {}", maxRetry);
//        }
//        if (maxRetry <= 0) {
//            throw new Exception500(ErrorMessage.GET_KAKAO_USER_ERROR);
//        }
//
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode node = null;
//        try {
//            node = mapper.readTree(responseBody);
//        } catch (Exception e) {
//            throw new Exception500(ErrorMessage.MAPPER_READ_TREE_ERROR);
//        }
//
//        return UserResponse.KakaoUserInfo.builder()
//                .oauthId(node.path("id").asLong())
//                .email(node.path("kakao_account").path("email").asText())
//                .name(node.path("kakao_account").path("name").asText())
//                .profileImageUrl(node.path("kakao_account").path("profileImageUrl").asText())
//                .build();
//    }
}
