package pl.pwr.bikeplanner.routing.infrastructure.graphhopper;

import com.graphhopper.json.Statement;
import com.graphhopper.util.CustomModel;
import org.junit.jupiter.api.Test;
import pl.pwr.bikeplanner.routing.domain.BaseProfile;
import pl.pwr.bikeplanner.routing.domain.ExplicitPreferences;
import pl.pwr.bikeplanner.routing.domain.SurfacePreference;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomModelFactoryTest {

    @Test
    void emptyPreferencesYieldNoPriorityRules() {
        CustomModel model = new CustomModelFactory(true).create(ExplicitPreferences.NONE);

        assertThat(model.getPriority()).isEmpty();
    }

    @Test
    void roadProfileHasNoSurfaceBias() {
        assertThat(CustomModelFactory.profileSurfaceBias(BaseProfile.ROAD).getPriority()).isEmpty();
    }

    @Test
    void gravelProfilePenalizesPavedModerately() {
        CustomModel model = CustomModelFactory.profileSurfaceBias(BaseProfile.GRAVEL);

        assertThat(conditions(model)).containsExactly(CustomModelFactory.PAVED);
        assertThat(values(model)).containsExactly(CustomModelFactory.GRAVEL_PROFILE_PAVED_PENALTY);
    }

    @Test
    void mtbProfilePenalizesPavedStrongly() {
        CustomModel model = CustomModelFactory.profileSurfaceBias(BaseProfile.MTB);

        assertThat(conditions(model)).containsExactly(CustomModelFactory.PAVED);
        assertThat(values(model)).containsExactly(CustomModelFactory.MTB_PROFILE_PAVED_PENALTY);
    }

    @Test
    void preferPavedPenalizesUnpavedSurfaces() {
        CustomModel model = new CustomModelFactory(false).create(
                new ExplicitPreferences(SurfacePreference.PREFER_PAVED, false, false));

        assertThat(conditions(model)).containsExactly(CustomModelFactory.UNPAVED);
        assertThat(values(model)).containsExactly(CustomModelFactory.UNPAVED_PENALTY);
    }

    @Test
    void preferUnpavedPenalizesPavedSurfaces() {
        CustomModel model = new CustomModelFactory(false).create(
                new ExplicitPreferences(SurfacePreference.PREFER_UNPAVED, false, false));

        assertThat(conditions(model)).containsExactly(CustomModelFactory.PAVED);
        assertThat(values(model)).containsExactly(CustomModelFactory.PAVED_PENALTY);
    }

    @Test
    void avoidMainRoadsAddsPrimaryAndSecondaryPenalties() {
        CustomModel model = new CustomModelFactory(false).create(
                new ExplicitPreferences(SurfacePreference.NO_PREFERENCE, true, false));

        assertThat(conditions(model)).containsExactly(
                "road_class == TRUNK || road_class == PRIMARY",
                "road_class == SECONDARY");
        assertThat(values(model)).containsExactly(
                CustomModelFactory.MAIN_ROAD_PENALTY,
                CustomModelFactory.SECONDARY_ROAD_PENALTY);
    }

    @Test
    void steepClimbRulesRequireElevation() {
        ExplicitPreferences preferences =
                new ExplicitPreferences(SurfacePreference.NO_PREFERENCE, false, true);

        assertThat(new CustomModelFactory(false).create(preferences).getPriority()).isEmpty();

        CustomModel withElevation = new CustomModelFactory(true).create(preferences);
        assertThat(conditions(withElevation)).containsExactly(
                "average_slope >= 8",
                "average_slope >= 4");
        assertThat(values(withElevation)).containsExactly(
                CustomModelFactory.STEEP_CLIMB_PENALTY,
                CustomModelFactory.MODERATE_CLIMB_PENALTY);
    }

    private static List<String> conditions(CustomModel model) {
        return model.getPriority().stream().map(Statement::condition).toList();
    }

    private static List<String> values(CustomModel model) {
        return model.getPriority().stream().map(Statement::value).toList();
    }
}
