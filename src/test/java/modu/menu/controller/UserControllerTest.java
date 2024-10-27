package modu.menu.controller;

import modu.menu.ControllerTestSupporter;
import modu.menu.controller.model.TempJoinRequest;
import modu.menu.controller.model.TempLoginRequest;
import modu.menu.service.model.TempJoinServiceResponse;
import modu.menu.service.model.TempLoginServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupporter {

    @DisplayName("새로운 회원을 등록한다.")
    @Test
    void crateUser() throws Exception {
        // given
        TempJoinRequest tempJoinRequest = TempJoinRequest.builder()
                .email("test1234@naver.com")
                .password("test1234")
                .name("홍길동")
                .nickname("test1234")
                .gender("M")
                .age(15)
                .birthdate(LocalDate.of(1997, 1, 1))
                .phoneNumber("01012345678")
                .build();

        // when
        when(userService.tempJoin(any()))
                .thenReturn(TempJoinServiceResponse.builder()
                        .id(1L)
                        .name(tempJoinRequest.getName())
                        .nickname(tempJoinRequest.getNickname())
                        .profileImageUrl("")
                        .accessToken("")
                        .build());

        // then
        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tempJoinRequest)))
                .andExpect(status().isOk());
    }

    @DisplayName("등록된 회원으로 로그인한다.")
    @Test
    void login() throws Exception {
        // given
        TempLoginRequest tempLoginRequest = TempLoginRequest.builder()
                .email("test1234@naver.com")
                .password("test1234")
                .build();

        // when
        when(userService.tempLogin(any()))
                .thenReturn(TempLoginServiceResponse.builder()
                        .id(1L)
                        .name("홍길동")
                        .nickname("test1234")
                        .profileImageUrl("")
                        .accessToken("token")
                        .build());

        // then
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tempLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "token"));
    }
}