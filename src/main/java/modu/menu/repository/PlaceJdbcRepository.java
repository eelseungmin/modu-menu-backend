package modu.menu.repository;

import modu.menu.domain.Place;

import java.util.List;

public interface PlaceJdbcRepository {

    void insertDummyData(List<Place> places);
}
