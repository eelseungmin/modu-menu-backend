package modu.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import modu.menu.core.auth.jwt.JwtProvider;
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
}
