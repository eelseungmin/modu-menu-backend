package modu.menu.repository;

import modu.menu.IntegrationTestSupporter;
import modu.menu.domain.Vibe;
import modu.menu.domain.VibeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class VibeRepositoryTest extends IntegrationTestSupporter {

    @Autowired
    private VibeRepository vibeRepository;

    @BeforeEach
    void setUp() {
        vibeRepository.saveAll(List.of(
                createVibe(VibeType.NOISY),
                createVibe(VibeType.MODERN),
                createVibe(VibeType.NICE_VIEW),
                createVibe(VibeType.QUIET),
                createVibe(VibeType.GOOD_SERVICE),
                createVibe(VibeType.TRENDY)));
    }

    @DisplayName("VibeType을 통해 분위기를 조회한다.")
    @Test
    void findByVibeType() {
        // given
        VibeType type = VibeType.NOISY;

        // when
        Optional<Vibe> vibeResult = vibeRepository.findByVibeType(type);

        // then
        assertThat(vibeResult).get()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("type", VibeType.NOISY);
    }

    private Vibe createVibe(VibeType type) {
        return Vibe.builder()
                .type(type)
                .build();
    }
}
