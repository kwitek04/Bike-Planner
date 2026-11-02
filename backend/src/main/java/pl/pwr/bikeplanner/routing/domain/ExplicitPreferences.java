package pl.pwr.bikeplanner.routing.domain;

import java.util.Objects;

/**
 * Preferencje jawne podawane przez użytkownika przy wyznaczaniu trasy.
 */
public record ExplicitPreferences(
        SurfacePreference surface,
        boolean avoidMainRoads,
        boolean avoidSteepClimbs) {

    public static final ExplicitPreferences NONE =
            new ExplicitPreferences(SurfacePreference.NO_PREFERENCE, false, false);

    public ExplicitPreferences {
        Objects.requireNonNull(surface, "surface");
    }

    public boolean isEmpty() {
        return equals(NONE);
    }
}
