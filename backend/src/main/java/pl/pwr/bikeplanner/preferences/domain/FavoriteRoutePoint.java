package pl.pwr.bikeplanner.preferences.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class FavoriteRoutePoint {

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lon;

    protected FavoriteRoutePoint() {
    }

    public FavoriteRoutePoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
