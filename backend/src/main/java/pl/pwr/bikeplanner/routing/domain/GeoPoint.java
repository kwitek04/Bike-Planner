package pl.pwr.bikeplanner.routing.domain;

public record GeoPoint(double latitude, double longitude) {

    public GeoPoint {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude out of range: " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude out of range: " + longitude);
        }
    }
}
