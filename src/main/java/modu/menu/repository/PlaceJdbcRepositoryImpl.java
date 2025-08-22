package modu.menu.repository;

import lombok.RequiredArgsConstructor;
import modu.menu.domain.Place;
import modu.menu.domain.PlaceVibe;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PlaceJdbcRepositoryImpl implements PlaceJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private static final int BATCH_SIZE = 1000;

    @Override
    public void insertDummyData(List<Place> places) {
        String sql = "INSERT INTO place_tb (id, name, address, ph, business_hours, menu, latitude, longitude, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (int start = 0; start < places.size(); start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE, places.size());
            List<Place> batchList = places.subList(start, end);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, batchList.get(i).getId());
                    ps.setString(2, batchList.get(i).getName());
                    ps.setString(3, batchList.get(i).getAddress());
                    ps.setString(4, batchList.get(i).getPh());
                    ps.setString(5, batchList.get(i).getBusinessHours());
                    ps.setString(6, batchList.get(i).getMenu());
                    ps.setDouble(7, batchList.get(i).getLatitude());
                    ps.setDouble(8, batchList.get(i).getLongitude());
                    ps.setString(9, batchList.get(i).getImageUrl());
                }

                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            });
        }
    }
}
