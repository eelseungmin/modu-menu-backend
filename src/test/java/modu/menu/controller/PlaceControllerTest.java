package modu.menu.controller;

import modu.menu.ControllerTestSupporter;
import modu.menu.controller.model.CategoryResponse;
import modu.menu.controller.model.SearchPlaceResponse;
import modu.menu.service.model.FoodTypeServiceResponse;
import modu.menu.service.model.SearchResultServiceResponse;
import modu.menu.service.model.VibeTypeServiceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlaceControllerTest extends ControllerTestSupporter {

    @DisplayName("카테고리 목록을 조회하면 성공한다.")
    @Test
    void getCategory() throws Exception {
        // given


        // when
        when(placeService.getCategory())
                .thenReturn(CategoryResponse.builder()
                        .foods(FoodTypeServiceResponse.getFoodTypeHierarchy())
                        .vibes(VibeTypeServiceResponse.toList())
                        .build());

        // then
        mockMvc.perform(get("/api/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.reason").value("OK"))
                .andExpect(jsonPath("$.data").isMap());
    }

    @DisplayName("음식점 후보를 검색하면 성공한다.")
    @Test
    void search() throws Exception {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 0;

        // when
        when(placeService.searchPlace(any(), any(), any(), any(), any()))
                .thenReturn(
                        SearchPlaceResponse.builder()
                                .results(List.of(
                                        SearchResultServiceResponse.builder()
                                                .id(1L)
                                                .name("서가앤쿡 노원역점")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(2L)
                                                .name("이자카야모리")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(3L)
                                                .name("타코벨")
                                                .build()
                                ))
                                .totalElements(3L)
                                .totalPages(1)
                                .currentPageNumber(0)
                                .isFirst(true)
                                .isLast(false)
                                .isEmpty(false)
                                .build()
                );

        // then
        mockMvc.perform(get("/api/place")
                        .queryParam("latitude", String.valueOf(latitude))
                        .queryParam("longitude", String.valueOf(longitude))
                        .queryParam("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.reason").value("OK"))
                .andExpect(jsonPath("$.data").isMap());
    }

    @DisplayName("음식점 후보를 검색할 때 위도, 경도를 생략할 경우 기본 값으로 요청된다.")
    @Test
    void searchByNoLatitudeAndLongitude() throws Exception {
        // given
        Integer page = 0;

        // when
        when(placeService.searchPlace(any(), any(), any(), any(), any()))
                .thenReturn(
                        SearchPlaceResponse.builder()
                                .results(List.of(
                                        SearchResultServiceResponse.builder()
                                                .id(1L)
                                                .name("서가앤쿡 노원역점")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(2L)
                                                .name("이자카야모리")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(3L)
                                                .name("타코벨")
                                                .build()
                                ))
                                .totalElements(3L)
                                .totalPages(1)
                                .currentPageNumber(0)
                                .isFirst(true)
                                .isLast(false)
                                .isEmpty(false)
                                .build()
                );

        // then
        mockMvc.perform(get("/api/place")
                        .queryParam("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.reason").value("OK"))
                .andExpect(jsonPath("$.data").isMap());
    }

    @DisplayName("음식점 후보를 검색할 때 위도는 양수이다.")
    @Test
    void searchByWrongLatitude() throws Exception {
        // given
        Double latitude = -1.0;
        Integer page = 0;

        // when
        when(placeService.searchPlace(any(), any(), any(), any(), any()))
                .thenReturn(
                        SearchPlaceResponse.builder()
                                .results(List.of(
                                        SearchResultServiceResponse.builder()
                                                .id(1L)
                                                .name("서가앤쿡 노원역점")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(2L)
                                                .name("이자카야모리")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(3L)
                                                .name("타코벨")
                                                .build()
                                ))
                                .totalElements(3L)
                                .totalPages(1)
                                .currentPageNumber(0)
                                .isFirst(true)
                                .isLast(false)
                                .isEmpty(false)
                                .build()
                );

        // then
        mockMvc.perform(get("/api/place")
                        .queryParam("latitude", String.valueOf(latitude))
                        .queryParam("page", String.valueOf(page)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.reason").value("Bad Request"))
                .andExpect(jsonPath("$.cause").exists())
                .andExpect(jsonPath("$.message").value("위도는 0 또는 양수여야 합니다."));
    }

    @DisplayName("음식점 후보를 검색할 때 경도는 양수이다.")
    @Test
    void searchByWrongLongitude() throws Exception {
        // given
        Double longitude = -1.0;
        Integer page = 0;

        // when
        when(placeService.searchPlace(any(), any(), any(), any(), any()))
                .thenReturn(
                        SearchPlaceResponse.builder()
                                .results(List.of(
                                        SearchResultServiceResponse.builder()
                                                .id(1L)
                                                .name("서가앤쿡 노원역점")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(2L)
                                                .name("이자카야모리")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(3L)
                                                .name("타코벨")
                                                .build()
                                ))
                                .totalElements(3L)
                                .totalPages(1)
                                .currentPageNumber(0)
                                .isFirst(true)
                                .isLast(false)
                                .isEmpty(false)
                                .build()
                );

        // then
        mockMvc.perform(get("/api/place")
                        .queryParam("longitude", String.valueOf(longitude))
                        .queryParam("page", String.valueOf(page)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.reason").value("Bad Request"))
                .andExpect(jsonPath("$.cause").exists())
                .andExpect(jsonPath("$.message").value("경도는 0 또는 양수여야 합니다."));
    }

    @DisplayName("음식점 후보를 검색할 때 정해진 음식 값을 입력해야 한다.")
    @Test
    void searchByWrongFoodType() throws Exception {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 0;

        // when
        when(placeService.searchPlace(any(), any(), any(), any(), any()))
                .thenReturn(
                        SearchPlaceResponse.builder()
                                .results(List.of(
                                        SearchResultServiceResponse.builder()
                                                .id(1L)
                                                .name("서가앤쿡 노원역점")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(2L)
                                                .name("이자카야모리")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(3L)
                                                .name("타코벨")
                                                .build()
                                ))
                                .totalElements(3L)
                                .totalPages(1)
                                .currentPageNumber(0)
                                .isFirst(true)
                                .isLast(false)
                                .isEmpty(false)
                                .build()
                );

        // then
        mockMvc.perform(get("/api/place")
                        .queryParam("latitude", String.valueOf(latitude))
                        .queryParam("longitude", String.valueOf(longitude))
                        .queryParam("food", "la")
                        .queryParam("page", String.valueOf(page)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.reason").value("Bad Request"))
                .andExpect(jsonPath("$.cause").exists())
                .andExpect(jsonPath("$.message").value("음식에 허용된 값만 입력해주세요."));
    }

    @DisplayName("음식점 후보를 검색할 때 정해진 분위기 값을 입력해야 한다.")
    @Test
    void searchByWrongVibeType() throws Exception {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = 0;

        // when
        when(placeService.searchPlace(any(), any(), any(), any(), any()))
                .thenReturn(
                        SearchPlaceResponse.builder()
                                .results(List.of(
                                        SearchResultServiceResponse.builder()
                                                .id(1L)
                                                .name("서가앤쿡 노원역점")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(2L)
                                                .name("이자카야모리")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(3L)
                                                .name("타코벨")
                                                .build()
                                ))
                                .totalElements(3L)
                                .totalPages(1)
                                .currentPageNumber(0)
                                .isFirst(true)
                                .isLast(false)
                                .isEmpty(false)
                                .build()
                );

        // then
        mockMvc.perform(get("/api/place")
                        .queryParam("latitude", String.valueOf(latitude))
                        .queryParam("longitude", String.valueOf(longitude))
                        .queryParam("vibe", "la")
                        .queryParam("page", String.valueOf(page)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.reason").value("Bad Request"))
                .andExpect(jsonPath("$.cause").exists())
                .andExpect(jsonPath("$.message").value("분위기에 허용된 값만 입력해주세요."));
    }

    @DisplayName("음식점 후보를 검색할 때 정해진 분위기 값을 입력해야 한다.")
    @Test
    void searchByWrongPageNumber() throws Exception {
        // given
        Double latitude = 37.6737992;
        Double longitude = 127.060022;
        Integer page = -1;

        // when
        when(placeService.searchPlace(any(), any(), any(), any(), any()))
                .thenReturn(
                        SearchPlaceResponse.builder()
                                .results(List.of(
                                        SearchResultServiceResponse.builder()
                                                .id(1L)
                                                .name("서가앤쿡 노원역점")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(2L)
                                                .name("이자카야모리")
                                                .build(),
                                        SearchResultServiceResponse.builder()
                                                .id(3L)
                                                .name("타코벨")
                                                .build()
                                ))
                                .totalElements(3L)
                                .totalPages(1)
                                .currentPageNumber(0)
                                .isFirst(true)
                                .isLast(false)
                                .isEmpty(false)
                                .build()
                );

        // then
        mockMvc.perform(get("/api/place")
                        .queryParam("latitude", String.valueOf(latitude))
                        .queryParam("longitude", String.valueOf(longitude))
                        .queryParam("page", String.valueOf(page)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.reason").value("Bad Request"))
                .andExpect(jsonPath("$.cause").exists())
                .andExpect(jsonPath("$.message").value("페이지 번호는 0 또는 양수여야 합니다."));
    }
}
