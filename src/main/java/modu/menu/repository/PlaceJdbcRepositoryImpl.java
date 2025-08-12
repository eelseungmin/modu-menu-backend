package modu.menu.repository;

import lombok.RequiredArgsConstructor;
import modu.menu.domain.Place;
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

    @Override
    public void insertDummyData(List<Place> places) {
        String sql = "INSERT INTO place_tb (id, name, address, ph, business_hours, menu, latitude, longitude, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, places.get(i).getId());
                ps.setString(2, places.get(i).getName());
                ps.setString(3, places.get(i).getAddress());
                ps.setString(4, places.get(i).getPh());
                ps.setString(5, places.get(i).getBusinessHours());
                ps.setString(6, places.get(i).getMenu());
                ps.setDouble(7, places.get(i).getLatitude());
                ps.setDouble(8, places.get(i).getLongitude());
                ps.setString(9, places.get(i).getImageUrl());
            }

            @Override
            public int getBatchSize() {
                return places.size();
            }
        });
    }
}
