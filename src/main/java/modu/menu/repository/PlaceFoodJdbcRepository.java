package modu.menu.repository;

import modu.menu.domain.PlaceFood;

import java.util.List;

public interface PlaceFoodJdbcRepository {

    void insertDummyData(List<PlaceFood> placeFoods);
}
