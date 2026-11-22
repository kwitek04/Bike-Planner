package pl.pwr.bikeplanner.preferences.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pwr.bikeplanner.preferences.domain.FavoriteRoute;
import pl.pwr.bikeplanner.preferences.domain.FavoriteRoutePoint;
import pl.pwr.bikeplanner.preferences.domain.FavoriteRouteRepository;
import pl.pwr.bikeplanner.routing.application.RoutePlanningService;
import pl.pwr.bikeplanner.routing.domain.PlannedRoute;

import java.util.List;

@Service
public class FavoriteRouteService {

    private final FavoriteRouteRepository favorites;
    private final RoutePlanningService routePlanningService;

    FavoriteRouteService(FavoriteRouteRepository favorites, RoutePlanningService routePlanningService) {
        this.favorites = favorites;
        this.routePlanningService = routePlanningService;
    }

    @Transactional
    public FavoriteRouteView save(long userId, String name, RouteDefinition definition) {
        PlannedRoute planned = routePlanningService.planRoute(definition.toQuery());
        RouteDefinition.Preferences preferences = definition.preferences();
        FavoriteRoute favorite = favorites.save(new FavoriteRoute(
                userId,
                name.trim(),
                definition.points().stream().map(p -> new FavoriteRoutePoint(p.lat(), p.lon())).toList(),
                definition.baseProfile(),
                preferences.surface(),
                preferences.avoidMainRoads(),
                preferences.avoidSteepClimbs(),
                planned.distanceMeters()));
        return toView(favorite);
    }

    @Transactional(readOnly = true)
    public List<FavoriteRouteView> list(long userId) {
        return favorites.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toView)
                .toList();
    }

    @Transactional
    public void delete(long userId, long favoriteId) {
        FavoriteRoute favorite = favorites.findByIdAndUserId(favoriteId, userId)
                .orElseThrow(() -> new FavoriteRouteNotFoundException(favoriteId));
        favorites.delete(favorite);
    }

    private FavoriteRouteView toView(FavoriteRoute favorite) {
        return new FavoriteRouteView(
                favorite.getId(),
                favorite.getName(),
                toDefinition(favorite),
                favorite.getDistanceMeters(),
                favorite.getCreatedAt());
    }

    private static RouteDefinition toDefinition(FavoriteRoute favorite) {
        return new RouteDefinition(
                favorite.getPoints().stream().map(p -> new RouteDefinition.Point(p.getLat(), p.getLon())).toList(),
                favorite.getBaseProfile(),
                new RouteDefinition.Preferences(
                        favorite.getSurface(), favorite.isAvoidMainRoads(), favorite.isAvoidSteepClimbs()));
    }
}
