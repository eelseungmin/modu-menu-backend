package modu.menu.repository;

import lombok.RequiredArgsConstructor;
import modu.menu.domain.PlaceVibe;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PlaceVibeJdbcRepositoryImpl implements PlaceVibeJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void insertDummyData(List<PlaceVibe> placeVibes) {
        String sql = "INSERT INTO place_vibe_tb (place_id, vibe_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, placeVibes.get(i).getPlace().getId());
                ps.setLong(2, placeVibes.get(i).getVibe().getId());
            }

            @Override
            public int getBatchSize() {
                return placeVibes.size();
            }
        });
    }
}
