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
    public void insertDummyData(List<Place> dummyPlaces) {
        String sql = "INSERT INTO place_tb (name, address, ph, business_hours, menu, latitude, longitude, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, dummyPlaces.get(i).getName());
                ps.setString(2, dummyPlaces.get(i).getAddress());
                ps.setString(3, dummyPlaces.get(i).getPh());
                ps.setString(4, dummyPlaces.get(i).getBusinessHours());
                ps.setString(5, dummyPlaces.get(i).getMenu());
                ps.setDouble(6, dummyPlaces.get(i).getLatitude());
                ps.setDouble(7, dummyPlaces.get(i).getLongitude());
                ps.setString(8, dummyPlaces.get(i).getImageUrl());
            }

            @Override
            public int getBatchSize() {
                return dummyPlaces.size();
            }
        });
    }
}
