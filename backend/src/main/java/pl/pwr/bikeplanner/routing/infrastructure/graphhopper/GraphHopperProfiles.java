package pl.pwr.bikeplanner.routing.infrastructure.graphhopper;

import com.graphhopper.config.LMProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.util.CustomModel;
import com.graphhopper.util.GHUtility;
import pl.pwr.bikeplanner.routing.domain.BaseProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Odwzorowanie profili bazowych systemu na profile GraphHoppera oparte na wbudowanych
 * modelach {@code racingbike.json}, {@code bike.json} i {@code mtb.json}.
 */
final class GraphHopperProfiles {

    static final List<String> ENCODED_VALUES = List.of(
            "racingbike_access", "racingbike_priority", "racingbike_average_speed",
            "bike_access", "bike_priority", "bike_average_speed",
            "mtb_access", "mtb_priority", "mtb_average_speed",
            "bike_road_access", "roundabout", "country",
            "road_class", "road_environment", "surface", "smoothness", "track_type",
            "mtb_rating", "hike_rating");

    static final List<String> ELEVATION_ENCODED_VALUES = List.of("average_slope", "max_slope");

    private GraphHopperProfiles() {
    }

    static String profileName(BaseProfile baseProfile) {
        return switch (baseProfile) {
            case ROAD -> "road";
            case GRAVEL -> "gravel";
            case MTB -> "mtb";
        };
    }

    private static String customModelFile(BaseProfile baseProfile) {
        return switch (baseProfile) {
            case ROAD -> "racingbike.json";
            case GRAVEL -> "bike.json";
            case MTB -> "mtb.json";
        };
    }

    static List<Profile> profiles(boolean elevationEnabled) {
        return Arrays.stream(BaseProfile.values())
                .map(p -> new Profile(profileName(p)).setCustomModel(baseModel(p, elevationEnabled)))
                .toList();
    }

    static List<LMProfile> landmarkProfiles() {
        return Arrays.stream(BaseProfile.values())
                .map(p -> new LMProfile(profileName(p)))
                .toList();
    }

    static String encodedValues(boolean elevationEnabled) {
        List<String> values = new ArrayList<>(ENCODED_VALUES);
        if (elevationEnabled) {
            values.addAll(ELEVATION_ENCODED_VALUES);
        }
        return String.join(",", values);
    }

    private static CustomModel baseModel(BaseProfile baseProfile, boolean elevationEnabled) {
        CustomModel model = GHUtility.loadCustomModelFromJar(customModelFile(baseProfile));
        model = CustomModel.merge(model, CustomModelFactory.profileSurfaceBias(baseProfile));
        if (elevationEnabled) {
            model = CustomModel.merge(model, GHUtility.loadCustomModelFromJar("bike_elevation.json"));
        }
        return model;
    }
}
