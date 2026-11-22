package pl.pwr.bikeplanner.preferences.application;

import java.time.Instant;

public record FavoriteRouteView(
        Long id,
        String name,
        RouteDefinition route,
        double distanceMeters,
        Instant createdAt) {
}
