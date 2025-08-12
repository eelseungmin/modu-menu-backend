package modu.menu.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import modu.menu.core.util.DistanceCalculator;
import modu.menu.domain.FoodType;
import modu.menu.domain.Place;
import modu.menu.domain.VibeType;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

import static modu.menu.domain.QFood.food;
import static modu.menu.domain.QPlace.place;
import static modu.menu.domain.QPlaceFood.placeFood;
import static modu.menu.domain.QPlaceVibe.placeVibe;
import static modu.menu.domain.QVibe.vibe;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PlaceQuerydslRepositoryImpl implements PlaceQuerydslRepository {

    private final JPAQueryFactory query;
    private static final int PAGE_SIZE = 20;

    /**
     * 검색 정책
     * 1) 검색 조건에 부합하는 식당을 노출하고,
     * 1-a) 가까운 거리순
     * 1-b) 거리 동일한 경우, 음식점명 가나다순
     * <p>
     * 2) 그 뒤에 검색 조건 중 '분위기'를 제외한 나머지 조건에 부합하는 식당 리스트 추가 노출
     * 2-a) 가까운 거리순
     * 2-b) 거리 동일한 경우, 음식점명 가나다순
     */
    @Override
    public Page<Place> findByCondition(Double latitude, Double longitude, List<FoodType> foods, List<VibeType> vibes, Integer page) {
        log.debug("======================first query start.======================");
        List<Place> firstPlaces = query.select(place)
                .from(place)
                .leftJoin(place.placeFoods, placeFood)
                .leftJoin(place.placeVibes, placeVibe)
                .leftJoin(placeFood.food, food)
                .leftJoin(placeVibe.vibe, vibe)
                .where(foodNames(foods), vibeNames(vibes))
                .fetch();
        log.debug("======================first places size: {}======================", firstPlaces.size());
        log.info("쿼리 실행 개수: {}, 쿼리 실행 시간: {}ms",
                QueryCountHolder.get("ProxyDataSource").getTotal(),
                QueryCountHolder.get("ProxyDataSource").getTime());
        log.debug("======================first query end.======================");

        log.debug("======================second query start.======================");
        List<Place> secondPlaces = query.select(place)
                .from(place)
                .leftJoin(place.placeFoods, placeFood)
                .leftJoin(placeFood.food, food)
                .where(foodNames(foods))
                .fetch();
        log.debug("======================second places size: {}======================", secondPlaces.size());
        log.info("쿼리 실행 개수: {}, 쿼리 실행 시간: {}ms",
                QueryCountHolder.get("ProxyDataSource").getTotal(),
                QueryCountHolder.get("ProxyDataSource").getTime());
        log.debug("======================second query end.======================");

        log.debug("======================sort start.======================");
        long startTime = System.currentTimeMillis();
        // 중복 제거 후 검색 정책에 따라 정렬
        List<Place> sortedPlaces = Stream.concat(firstPlaces.stream(), secondPlaces.stream())
                .distinct()
                .filter(place -> DistanceCalculator.calculate(latitude, longitude, place.getLatitude(), place.getLongitude()) <= 1000.0)
                .sorted((place1, place2) -> {
                    double distance1 = DistanceCalculator.calculate(latitude, longitude, place1.getLatitude(), place1.getLongitude());
                    double distance2 = DistanceCalculator.calculate(latitude, longitude, place2.getLatitude(), place2.getLongitude());

                    if (distance1 == distance2) {
                        return place1.getName().compareTo(place2.getName());
                    }
                    if (distance1 > distance2) {
                        return 1;
                    } else {
                        return -1;
                    }
                })
                .toList();
        log.info("정렬 시간: {}ms", System.currentTimeMillis() - startTime);
        log.debug("======================sort end.======================");

        return new PageImpl<>(
                sortedPlaces.subList(Math.min(page * PAGE_SIZE, sortedPlaces.size()),
                        Math.min((page + 1) * PAGE_SIZE, sortedPlaces.size())),
                PageRequest.of(page, PAGE_SIZE),
                sortedPlaces.size()
        );
    }

    private BooleanExpression foodNames(List<FoodType> foods) {
        if (foods == null || foods.isEmpty()) {
            return null;
        }
        return food.type.in(foods);
    }

    private BooleanExpression vibeNames(List<VibeType> vibes) {
        if (vibes == null || vibes.isEmpty()) {
            return null;
        }
        return vibe.type.in(vibes);
    }
}
