package pl.pwr.bikeplanner.routing.infrastructure.graphhopper;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.DistanceCalcEarth;
import com.graphhopper.util.PointList;
import com.graphhopper.util.details.PathDetail;
import com.graphhopper.util.exceptions.ConnectionNotFoundException;
import com.graphhopper.util.exceptions.PointNotFoundException;
import com.graphhopper.util.exceptions.PointOutOfBoundsException;
import com.graphhopper.util.shapes.GHPoint;
import pl.pwr.bikeplanner.routing.application.RoutingEngine;
import pl.pwr.bikeplanner.routing.domain.ExplicitPreferences;
import pl.pwr.bikeplanner.routing.domain.PlannedRoute;
import pl.pwr.bikeplanner.routing.domain.PlannedRoute.SurfaceShare;
import pl.pwr.bikeplanner.routing.domain.RouteNotFoundException;
import pl.pwr.bikeplanner.routing.domain.RouteQuery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Adapter portu {@link RoutingEngine} wykorzystujący osadzoną bibliotekę GraphHopper.
 */
class GraphHopperRoutingEngine implements RoutingEngine {

    private static final String SURFACE_DETAIL = "surface";

    private final GraphHopper hopper;
    private final CustomModelFactory customModelFactory;

    GraphHopperRoutingEngine(GraphHopper hopper, CustomModelFactory customModelFactory) {
        this.hopper = hopper;
        this.customModelFactory = customModelFactory;
    }

    @Override
    public PlannedRoute route(RouteQuery query) {
        GHResponse response = hopper.route(toRequest(query));
        if (response.hasErrors()) {
            throw translate(response.getErrors());
        }
        return toPlannedRoute(response.getBest());
    }

    private GHRequest toRequest(RouteQuery query) {
        List<GHPoint> points = query.points().stream()
                .map(p -> new GHPoint(p.latitude(), p.longitude()))
                .toList();
        GHRequest request = new GHRequest(points)
                .setProfile(GraphHopperProfiles.profileName(query.baseProfile()))
                .setLocale(Locale.forLanguageTag("pl"))
                .setPathDetails(List.of(SURFACE_DETAIL));

        ExplicitPreferences preferences = query.preferences();
        if (!preferences.isEmpty()) {
            request.setCustomModel(customModelFactory.create(preferences));
        }
        return request;
    }

    private static PlannedRoute toPlannedRoute(ResponsePath path) {
        PointList points = path.getPoints();
        List<double[]> coordinates = new ArrayList<>(points.size());
        for (int i = 0; i < points.size(); i++) {
            coordinates.add(points.is3D()
                    ? new double[]{points.getLon(i), points.getLat(i), points.getEle(i)}
                    : new double[]{points.getLon(i), points.getLat(i)});
        }
        return new PlannedRoute(
                coordinates,
                path.getDistance(),
                Math.round(path.getTime() / 1000.0),
                path.getAscend(),
                path.getDescend(),
                surfaceShares(points, path.getPathDetails().getOrDefault(SURFACE_DETAIL, List.of()),
                        path.getDistance()));
    }

    private static List<SurfaceShare> surfaceShares(PointList points, List<PathDetail> details, double total) {
        Map<String, Double> distanceBySurface = new LinkedHashMap<>();
        for (PathDetail detail : details) {
            double distance = 0;
            for (int i = detail.getFirst(); i < detail.getLast(); i++) {
                distance += DistanceCalcEarth.DIST_EARTH.calcDist(
                        points.getLat(i), points.getLon(i), points.getLat(i + 1), points.getLon(i + 1));
            }
            distanceBySurface.merge(String.valueOf(detail.getValue()), distance, Double::sum);
        }
        return distanceBySurface.entrySet().stream()
                .map(e -> new SurfaceShare(e.getKey(), e.getValue(), total > 0 ? e.getValue() / total : 0))
                .sorted(Comparator.comparingDouble(SurfaceShare::distanceMeters).reversed())
                .toList();
    }

    private static RuntimeException translate(List<Throwable> errors) {
        Throwable first = errors.getFirst();
        if (first instanceof ConnectionNotFoundException
                || first instanceof PointNotFoundException
                || first instanceof PointOutOfBoundsException) {
            return new RouteNotFoundException(first.getMessage());
        }
        if (first instanceof IllegalArgumentException iae) {
            return iae;
        }
        return new IllegalStateException("Routing failed: " + errors, first);
    }
}
