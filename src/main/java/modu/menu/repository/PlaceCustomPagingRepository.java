//package modu.menu.place.reposiotry;
//
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
//import modu.menu.domain.FoodType;
//import modu.menu.domain.Place;
//import modu.menu.domain.VibeType;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//import static modu.menu.core.util.DistanceCalculator.calculate;
//
//// 현재 위치에서 1km 이내의 모든 음식점을 대상으로 가까운 거리 순으로 정렬하고, 거리가 같은 경우엔 음식점명을 가나다 순으로 정렬한 뒤 페이지를 반환한다.
//// 따라서 페이징 쿼리를 실행하는 것이 아니라, 조건과 일치하는 모든 음식점을 일단 불러온 뒤에 정렬을 수행하고 페이징을 해야 한다.
//@SuppressWarnings("unchecked")
//@RequiredArgsConstructor
//@Repository
//public class PlaceCustomPagingRepository {
//
//    private final EntityManager entityManager;
//    private static final int PAGE_SIZE = 20;
//
//    public Page<Place> findAll(Double latitude, Double longitude, Integer page) {
//        List<Place> places = entityManager.createQuery(
//                        "select p " +
//                                "from Place p " +
//                                "join PlaceFood pf on pf.place.id = p.id " +
//                                "join PlaceVibe pv on pv.place.id = p.id " +
//                                "join fetch Food f on f.id = pf.food.id " +
//                                "join fetch Vibe v on v.id = pv.vibe.id")
//                .getResultList();
//
//        return getTargetPage(latitude, longitude, page, places);
//    }
//
//    public Page<Place> findByVibeTypes(Double latitude, Double longitude, Integer page, List<VibeType> vibes) {
//        List<Place> places = entityManager.createQuery(
//                        "select p " +
//                                "from Place p " +
//                                "join PlaceFood pf on pf.place.id = p.id " +
//                                "join PlaceVibe pv on pv.place.id = p.id " +
//                                "join fetch Food f on f.id = pf.food.id " +
//                                "join fetch Vibe v on v.id = pv.vibe.id " +
//                                "where v.type in :vibes")
//                .setParameter("vibes", vibes)
//                .getResultList();
//
//        return getTargetPage(latitude, longitude, page, places);
//    }
//
//    public Page<Place> findByFoodTypes(Double latitude, Double longitude, Integer page, List<FoodType> foods) {
//        List<Place> places = entityManager.createQuery(
//                        "select p " +
//                                "from Place p " +
//                                "join PlaceFood pf on pf.place.id = p.id " +
//                                "join PlaceVibe pv on pv.place.id = p.id " +
//                                "join fetch Food f on f.id = pf.food.id " +
//                                "join fetch Vibe v on v.id = pv.vibe.id " +
//                                "where f.type in :foods")
//                .setParameter("foods", foods)
//                .getResultList();
//
//        return getTargetPage(latitude, longitude, page, places);
//    }
//
//    public Page<Place> findByFoodTypesAndVibeTypes(Double latitude, Double longitude, Integer page, List<FoodType> foods, List<VibeType> vibes) {
//        List<Place> places = entityManager.createQuery(
//                        "select p " +
//                                "from Place p " +
//                                "join PlaceFood pf on pf.place.id = p.id " +
//                                "join PlaceVibe pv on pv.place.id = p.id " +
//                                "join fetch Food f on f.id = pf.food.id " +
//                                "join fetch Vibe v on v.id = pv.vibe.id " +
//                                "where f.type in :foods and v.type in :vibes")
//                .setParameter("foods", foods)
//                .setParameter("vibes", vibes)
//                .getResultList();
//
//        return getTargetPage(latitude, longitude, page, places);
//    }
//
//    private PageImpl<Place> getTargetPage(Double latitude, Double longitude, Integer page, List<Place> places) {
//        List<Place> sortedPlaces = places.stream()
//                .filter(place -> calculate(latitude, longitude, place.getLatitude(), place.getLongitude()) <= 1000.0)
//                .sorted((place1, place2) -> {
//                    double distance1 = calculate(latitude, longitude, place1.getLatitude(), place1.getLongitude());
//                    double distance2 = calculate(latitude, longitude, place2.getLatitude(), place2.getLongitude());
//
//                    if (distance1 == distance2) {
//                        return place1.getName().compareTo(place2.getName());
//                    }
//                    if (distance1 > distance2) {
//                        return 1;
//                    } else {
//                        return -1;
//                    }
//                })
//                .toList();
//
//        // 페이지 번호가 음수인 경우는 컨트롤러에서 걸러내므로 여기서는 고려할 필요 없다.
//        // 다만 리스트의 크기가 page * SIZE보다 작은 경우를 고려해 아래와 같이 작성했다.
//        int start = Math.min(page * PAGE_SIZE, sortedPlaces.size());
//        int end = Math.min((page + 1) * PAGE_SIZE, sortedPlaces.size());
//
//        // start가 0, end가 10일 경우 subList에 의해 전체 리스트의 0부터 9번째 요소까지 페이징한다.
//        List<Place> pageContent = sortedPlaces.subList(start, end);
//
//        return new PageImpl<>(pageContent, PageRequest.of(page, PAGE_SIZE), sortedPlaces.size());
//    }
//}
