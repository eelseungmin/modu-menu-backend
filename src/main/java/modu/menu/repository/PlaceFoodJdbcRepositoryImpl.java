package modu.menu.repository;

import lombok.RequiredArgsConstructor;
import modu.menu.domain.Place;
import modu.menu.domain.PlaceFood;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PlaceFoodJdbcRepositoryImpl implements PlaceFoodJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final int BATCH_SIZE = 1000;

    @Override
    public void insertDummyData(List<PlaceFood> placeFoods) {
        String sql = "INSERT INTO place_food_tb (place_id, food_id) VALUES (?, ?)";

        for (int start = 0; start < placeFoods.size(); start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE, placeFoods.size());
            List<PlaceFood> batchList = placeFoods.subList(start, end);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, batchList.get(i).getPlace().getId());
                    ps.setLong(2, batchList.get(i).getFood().getId());
                }

                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            });
        }
    }
}
