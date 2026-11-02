package pl.pwr.bikeplanner.routing.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.pwr.bikeplanner.routing.domain.BaseProfile;
import pl.pwr.bikeplanner.routing.domain.ExplicitPreferences;
import pl.pwr.bikeplanner.routing.domain.GeoPoint;
import pl.pwr.bikeplanner.routing.domain.RouteQuery;
import pl.pwr.bikeplanner.routing.domain.SurfacePreference;

import java.util.List;

record RouteRequest(
        @NotNull @Size(min = 2, max = 10) List<@Valid @NotNull Point> points,
        @NotNull BaseProfile baseProfile,
        @Valid Preferences preferences) {

    record Point(
            @DecimalMin("-90") @DecimalMax("90") double lat,
            @DecimalMin("-180") @DecimalMax("180") double lon) {
    }

    record Preferences(SurfacePreference surface, boolean avoidMainRoads, boolean avoidSteepClimbs) {
    }

    RouteQuery toQuery() {
        ExplicitPreferences explicit = preferences == null
                ? ExplicitPreferences.NONE
                : new ExplicitPreferences(
                        preferences.surface() == null ? SurfacePreference.NO_PREFERENCE : preferences.surface(),
                        preferences.avoidMainRoads(),
                        preferences.avoidSteepClimbs());
        return new RouteQuery(
                points.stream().map(p -> new GeoPoint(p.lat(), p.lon())).toList(),
                baseProfile,
                explicit);
    }
}
