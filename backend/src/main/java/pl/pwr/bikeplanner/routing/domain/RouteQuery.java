package pl.pwr.bikeplanner.routing.domain;

import java.util.List;
import java.util.Objects;

/**
 * Zapytanie o trasę: punkt początkowy, opcjonalne punkty pośrednie, punkt końcowy
 * oraz opcje wybrane przez użytkownika.
 */
public record RouteQuery(
        List<GeoPoint> points,
        BaseProfile baseProfile,
        ExplicitPreferences preferences) {

    public RouteQuery {
        Objects.requireNonNull(baseProfile, "baseProfile");
        Objects.requireNonNull(preferences, "preferences");
        if (points == null || points.size() < 2) {
            throw new IllegalArgumentException("A route needs at least a start and an end point");
        }
        points = List.copyOf(points);
    }
}
