package pl.pwr.bikeplanner.routing.application;

import pl.pwr.bikeplanner.routing.domain.PlannedRoute;
import pl.pwr.bikeplanner.routing.domain.RouteNotFoundException;
import pl.pwr.bikeplanner.routing.domain.RouteQuery;

public interface RoutingEngine {

    /**
     * @throws RouteNotFoundException gdy między punktami nie istnieje przejezdna trasa
     */
    PlannedRoute route(RouteQuery query);
}
