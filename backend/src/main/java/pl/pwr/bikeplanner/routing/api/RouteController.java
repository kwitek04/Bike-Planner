package pl.pwr.bikeplanner.routing.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pwr.bikeplanner.routing.application.RoutePlanningService;

@RestController
@RequestMapping("/api/v1/routes")
class RouteController {

    private final RoutePlanningService routePlanningService;

    RouteController(RoutePlanningService routePlanningService) {
        this.routePlanningService = routePlanningService;
    }

    @PostMapping
    RouteResponse planRoute(@Valid @RequestBody RouteRequest request) {
        return RouteResponse.from(routePlanningService.planRoute(request.toQuery()));
    }
}
