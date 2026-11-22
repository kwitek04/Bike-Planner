package pl.pwr.bikeplanner.preferences.application;

import pl.pwr.bikeplanner.routing.domain.BaseProfile;
import pl.pwr.bikeplanner.routing.domain.ExplicitPreferences;
import pl.pwr.bikeplanner.routing.domain.GeoPoint;
import pl.pwr.bikeplanner.routing.domain.RouteQuery;
import pl.pwr.bikeplanner.routing.domain.SurfacePreference;

import java.util.List;
import java.util.Objects;

public record RouteDefinition(List<Point> points, BaseProfile baseProfile, Preferences preferences) {

    public RouteDefinition {
        points = List.copyOf(points);
        Objects.requireNonNull(baseProfile, "baseProfile");
        preferences = preferences == null ? Preferences.NONE : preferences;
    }

    public record Point(double lat, double lon) {
    }

    public record Preferences(SurfacePreference surface, boolean avoidMainRoads, boolean avoidSteepClimbs) {

        static final Preferences NONE = new Preferences(SurfacePreference.NO_PREFERENCE, false, false);

        public Preferences {
            surface = surface == null ? SurfacePreference.NO_PREFERENCE : surface;
        }
    }

    RouteQuery toQuery() {
        return new RouteQuery(
                points.stream().map(p -> new GeoPoint(p.lat(), p.lon())).toList(),
                baseProfile,
                new ExplicitPreferences(
                        preferences.surface(), preferences.avoidMainRoads(), preferences.avoidSteepClimbs()));
    }
}
