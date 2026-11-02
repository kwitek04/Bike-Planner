package pl.pwr.bikeplanner.routing.application;

import org.springframework.stereotype.Service;
import pl.pwr.bikeplanner.routing.domain.PlannedRoute;
import pl.pwr.bikeplanner.routing.domain.RouteQuery;

@Service
public class RoutePlanningService {

    private final RoutingEngine routingEngine;

    public RoutePlanningService(RoutingEngine routingEngine) {
        this.routingEngine = routingEngine;
    }

    public PlannedRoute planRoute(RouteQuery query) {
        return routingEngine.route(query);
    }
}
