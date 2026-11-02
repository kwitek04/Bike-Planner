package pl.pwr.bikeplanner.routing.infrastructure.graphhopper;

import com.graphhopper.util.CustomModel;
import pl.pwr.bikeplanner.routing.domain.BaseProfile;
import pl.pwr.bikeplanner.routing.domain.ExplicitPreferences;

import static com.graphhopper.json.Statement.ElseIf;
import static com.graphhopper.json.Statement.If;
import static com.graphhopper.json.Statement.Op.MULTIPLY;

class CustomModelFactory {

    static final String UNPAVED = String.join(" || ",
            "surface == UNPAVED", "surface == COMPACTED", "surface == FINE_GRAVEL", "surface == GRAVEL",
            "surface == GROUND", "surface == DIRT", "surface == GRASS", "surface == SAND");
    static final String PAVED = String.join(" || ",
            "surface == PAVED", "surface == ASPHALT", "surface == CONCRETE", "surface == PAVING_STONES");

    static final String UNPAVED_PENALTY = "0.4";
    static final String PAVED_PENALTY = "0.6";
    static final String GRAVEL_PROFILE_PAVED_PENALTY = "0.5";
    static final String MTB_PROFILE_PAVED_PENALTY = "0.3";
    static final String MAIN_ROAD_PENALTY = "0.3";
    static final String SECONDARY_ROAD_PENALTY = "0.6";
    static final String STEEP_CLIMB_PENALTY = "0.4";
    static final String MODERATE_CLIMB_PENALTY = "0.7";

    private final boolean elevationAvailable;

    CustomModelFactory(boolean elevationAvailable) {
        this.elevationAvailable = elevationAvailable;
    }

    static CustomModel profileSurfaceBias(BaseProfile profile) {
        CustomModel model = new CustomModel();
        switch (profile) {
            case ROAD -> { }
            case GRAVEL -> model.addToPriority(If(PAVED, MULTIPLY, GRAVEL_PROFILE_PAVED_PENALTY));
            case MTB -> model.addToPriority(If(PAVED, MULTIPLY, MTB_PROFILE_PAVED_PENALTY));
        }
        return model;
    }

    CustomModel create(ExplicitPreferences preferences) {
        CustomModel model = new CustomModel();

        switch (preferences.surface()) {
            case PREFER_PAVED -> model.addToPriority(If(UNPAVED, MULTIPLY, UNPAVED_PENALTY));
            case PREFER_UNPAVED -> model.addToPriority(If(PAVED, MULTIPLY, PAVED_PENALTY));
            case NO_PREFERENCE -> { }
        }

        if (preferences.avoidMainRoads()) {
            model.addToPriority(If("road_class == TRUNK || road_class == PRIMARY", MULTIPLY, MAIN_ROAD_PENALTY));
            model.addToPriority(ElseIf("road_class == SECONDARY", MULTIPLY, SECONDARY_ROAD_PENALTY));
        }

        if (preferences.avoidSteepClimbs() && elevationAvailable) {
            model.addToPriority(If("average_slope >= 8", MULTIPLY, STEEP_CLIMB_PENALTY));
            model.addToPriority(ElseIf("average_slope >= 4", MULTIPLY, MODERATE_CLIMB_PENALTY));
        }

        return model;
    }
}
