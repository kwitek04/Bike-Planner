package pl.pwr.bikeplanner.routing.domain;

import java.util.List;

/**
 * Wyznaczona trasa wraz z parametrami.
 */
public record PlannedRoute(
        List<double[]> coordinates,
        double distanceMeters,
        long durationSeconds,
        double ascentMeters,
        double descentMeters,
        List<SurfaceShare> surfaceShares) {

    public PlannedRoute {
        coordinates = List.copyOf(coordinates);
        surfaceShares = List.copyOf(surfaceShares);
    }

    public record SurfaceShare(String surface, double distanceMeters, double share) {
    }
}
