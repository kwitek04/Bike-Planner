package pl.pwr.bikeplanner.preferences.api;

import pl.pwr.bikeplanner.preferences.application.FavoriteRouteView;
import pl.pwr.bikeplanner.preferences.application.RouteDefinition;

import java.time.Instant;

record FavoriteRouteResponse(
        Long id,
        String name,
        RouteDefinition route,
        double distanceMeters,
        Instant createdAt) {

    static FavoriteRouteResponse from(FavoriteRouteView view) {
        return new FavoriteRouteResponse(
                view.id(), view.name(), view.route(), view.distanceMeters(), view.createdAt());
    }
}
