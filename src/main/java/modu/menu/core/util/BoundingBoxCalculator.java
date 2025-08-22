package modu.menu.core.util;

public class BoundingBoxCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    public static class BoundingBox {
        private final double minLat;
        private final double maxLat;
        private final double minLon;
        private final double maxLon;

        public BoundingBox(double minLat, double maxLat, double minLon, double maxLon) {
            this.minLat = minLat;
            this.maxLat = maxLat;
            this.minLon = minLon;
            this.maxLon = maxLon;
        }

        public double getMinLat() { return minLat; }
        public double getMaxLat() { return maxLat; }
        public double getMinLon() { return minLon; }
        public double getMaxLon() { return maxLon; }
    }

    // 주어진 위치와 반경(km)으로 bounding box 계산
    public static BoundingBox calculateBoundingBox(double latitude, double longitude, double radiusKm) {
        double latRad = Math.toRadians(latitude);
        double lonRad = Math.toRadians(longitude);

        double angular = radiusKm / EARTH_RADIUS_KM;

        double minLat = latRad - angular;
        double maxLat = latRad + angular;

        double deltaLon = Math.asin(Math.sin(angular) / Math.cos(latRad));
        double minLon = lonRad - deltaLon;
        double maxLon = lonRad + deltaLon;

        return new BoundingBox(
                Math.toDegrees(minLat),
                Math.toDegrees(maxLat),
                Math.toDegrees(minLon),
                Math.toDegrees(maxLon)
        );
    }
}
