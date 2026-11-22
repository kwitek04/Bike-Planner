package pl.pwr.bikeplanner.preferences.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.pwr.bikeplanner.preferences.application.FavoriteRouteNotFoundException;
import pl.pwr.bikeplanner.routing.domain.RouteNotFoundException;

@RestControllerAdvice(assignableTypes = FavoriteRouteController.class)
class PreferencesExceptionHandler {

    @ExceptionHandler(FavoriteRouteNotFoundException.class)
    ProblemDetail favoriteNotFound(FavoriteRouteNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problem.setTitle("Favorite route not found");
        return problem;
    }

    @ExceptionHandler(RouteNotFoundException.class)
    ProblemDetail routeNotFound(RouteNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, e.getMessage());
        problem.setTitle("Route not found");
        return problem;
    }
}
