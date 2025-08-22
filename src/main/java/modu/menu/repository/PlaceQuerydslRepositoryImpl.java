package modu.menu.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import modu.menu.core.util.BoundingBoxCalculator;
import modu.menu.domain.FoodType;
import modu.menu.domain.VibeType;
import modu.menu.service.model.PlaceFlatDto;
import modu.menu.service.model.PlaceVibeDto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static modu.menu.domain.QFood.food;
import static modu.menu.domain.QPlace.place;
import static modu.menu.domain.QPlaceFood.placeFood;
import static modu.menu.domain.QPlaceVibe.placeVibe;
import static modu.menu.domain.QVibe.vibe;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PlaceQuerydslRepositoryImpl implements PlaceQuerydslRepository {

    private final JPAQueryFactory queryFactory;
    // 사용자 위치 기준 2KM 반경으로 1차 필터링
    private static final double SEARCH_RADIUS_KM = 2.0;

    /**
     * 검색 정책
     * 1) 검색 조건에 부합하는 식당을 노출하고,
     * 1-a) 가까운 거리순
     * 1-b) 거리 동일한 경우, 음식점명 가나다순
     */
    @Override
    public List<PlaceFlatDto> findByCondition(Double latitude, Double longitude, List<FoodType> foods, List<VibeType> vibes) {
        JPAQuery<PlaceFlatDto> query = queryFactory
                .select(Projections.constructor(PlaceFlatDto.class,
                        place.id,
                        place.latitude,
                        place.longitude
                ))
                .from(place);

        if (foods != null && !foods.isEmpty()) {
            query.join(place.placeFoods, placeFood)
                    .join(placeFood.food, food);
        }
        if (vibes != null && !vibes.isEmpty()) {
            query.join(place.placeVibes, placeVibe)
                    .join(placeVibe.vibe, vibe);
        }

        // bounding box 설정
        BoundingBoxCalculator.BoundingBox boundingBox = BoundingBoxCalculator.calculateBoundingBox(latitude, longitude, SEARCH_RADIUS_KM);
        log.debug("minLat: {}, maxLat: {}, minLon: {}, maxLon: {}", boundingBox.getMinLat(), boundingBox.getMaxLat(), boundingBox.getMinLon(), boundingBox.getMaxLon());
        // WHERE 조건
        BooleanBuilder whereCondition = new BooleanBuilder();
        whereCondition
                .and(place.latitude.between(boundingBox.getMinLat(), boundingBox.getMaxLat()))
                .and(place.longitude.between(boundingBox.getMinLon(), boundingBox.getMaxLon()));
        if (foods != null && !foods.isEmpty()) {
            whereCondition.and(food.type.in(foods));
        }
        if (vibes != null && !vibes.isEmpty()) {
            whereCondition.and(vibe.type.in(vibes));
        }

        query.where(whereCondition);

        // GROUP BY와 HAVING으로 AND 조건 구현
        if ((foods != null && !foods.isEmpty()) || (vibes != null && !vibes.isEmpty())) {
            query.groupBy(place.id);

            // HAVING 조건
            BooleanBuilder havingCondition = new BooleanBuilder();
            if (foods != null && !foods.isEmpty()) {
                havingCondition.and(food.type.count().eq((long) foods.size()));
            }
            if (vibes != null && !vibes.isEmpty()) {
                havingCondition.and(vibe.type.count().eq((long) vibes.size()));
            }

            query.having(havingCondition);
        }

        return query.fetch();
    }

    @Override
    public List<PlaceVibeDto> findAllVibesByPlaceIds(List<Long> placeIds) {
        if (placeIds == null || placeIds.isEmpty()) {
            return new ArrayList<>();
        }

        return queryFactory
                .select(Projections.constructor(PlaceVibeDto.class,
                        place.id,
                        vibe.type
                ))
                .from(place)
                .leftJoin(place.placeVibes, placeVibe)
                .leftJoin(placeVibe.vibe, vibe)
                .where(place.id.in(placeIds))
                .fetch();
    }

    @Override
    public List<PlaceFoodDto> findAllFoodsByPlaceIds(List<Long> placeIds) {
        if (placeIds == null || placeIds.isEmpty()) {
            return new ArrayList<>();
        }

        return queryFactory
                .select(Projections.constructor(PlaceFoodDto.class,
                        place.id,
                        place.name,
                        place.address,
                        place.imageUrl,
                        food.type
                ))
                .from(place)
                .join(place.placeFoods, placeFood)
                .join(placeFood.food, food)
                .where(place.id.in(placeIds))
                .fetch();
    }
}
