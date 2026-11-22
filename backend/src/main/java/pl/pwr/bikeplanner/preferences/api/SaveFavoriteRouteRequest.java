package pl.pwr.bikeplanner.preferences.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.pwr.bikeplanner.preferences.application.RouteDefinition;
import pl.pwr.bikeplanner.routing.domain.BaseProfile;
import pl.pwr.bikeplanner.routing.domain.SurfacePreference;

import java.util.List;

record SaveFavoriteRouteRequest(
        @NotBlank @Size(max = 255) String name,
        @NotNull @Size(min = 2, max = 10) List<@Valid @NotNull Point> points,
        @NotNull BaseProfile baseProfile,
        @Valid Preferences preferences) {

    record Point(
            @DecimalMin("-90") @DecimalMax("90") double lat,
            @DecimalMin("-180") @DecimalMax("180") double lon) {
    }

    record Preferences(SurfacePreference surface, boolean avoidMainRoads, boolean avoidSteepClimbs) {
    }

    RouteDefinition toDefinition() {
        return new RouteDefinition(
                points.stream().map(p -> new RouteDefinition.Point(p.lat(), p.lon())).toList(),
                baseProfile,
                preferences == null
                        ? null
                        : new RouteDefinition.Preferences(
                                preferences.surface(), preferences.avoidMainRoads(), preferences.avoidSteepClimbs()));
    }
}
