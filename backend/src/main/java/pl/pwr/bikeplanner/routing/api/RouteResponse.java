package pl.pwr.bikeplanner.routing.api;

import pl.pwr.bikeplanner.routing.domain.PlannedRoute;

import java.util.List;

/**
 * Odpowiedź z geometrią w formacie GeoJSON (LineString), bezpośrednio obsługiwanym przez Leaflet.
 */
record RouteResponse(
        Geometry geometry,
        double distanceMeters,
        long durationSeconds,
        double ascentMeters,
        double descentMeters,
        List<PlannedRoute.SurfaceShare> surfaceShares) {

    record Geometry(String type, List<double[]> coordinates) {
    }

    static RouteResponse from(PlannedRoute route) {
        return new RouteResponse(
                new Geometry("LineString", route.coordinates()),
                route.distanceMeters(),
                route.durationSeconds(),
                route.ascentMeters(),
                route.descentMeters(),
                route.surfaceShares());
    }
}
