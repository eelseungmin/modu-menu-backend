package modu.menu.repository;

import modu.menu.domain.PlaceFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceFoodRepository extends JpaRepository<PlaceFood, Long>, PlaceFoodJdbcRepository {
}
