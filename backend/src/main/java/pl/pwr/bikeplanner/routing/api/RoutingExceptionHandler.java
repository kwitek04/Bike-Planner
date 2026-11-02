package pl.pwr.bikeplanner.routing.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.pwr.bikeplanner.routing.domain.RouteNotFoundException;

@RestControllerAdvice(assignableTypes = RouteController.class)
class RoutingExceptionHandler {

    @ExceptionHandler(RouteNotFoundException.class)
    ProblemDetail routeNotFound(RouteNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, e.getMessage());
        problem.setTitle("Route not found");
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail invalidRequest(IllegalArgumentException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problem.setTitle("Invalid route request");
        return problem;
    }
}
