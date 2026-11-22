package pl.pwr.bikeplanner.preferences.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.pwr.bikeplanner.preferences.application.FavoriteRouteService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/favorites")
class FavoriteRouteController {

    private final FavoriteRouteService favoriteRouteService;

    FavoriteRouteController(FavoriteRouteService favoriteRouteService) {
        this.favoriteRouteService = favoriteRouteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    FavoriteRouteResponse save(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody SaveFavoriteRouteRequest request) {
        return FavoriteRouteResponse.from(
                favoriteRouteService.save(userId(jwt), request.name(), request.toDefinition()));
    }

    @GetMapping
    List<FavoriteRouteResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return favoriteRouteService.list(userId(jwt)).stream()
                .map(FavoriteRouteResponse::from)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable long id) {
        favoriteRouteService.delete(userId(jwt), id);
    }

    private static long userId(Jwt jwt) {
        return Long.parseLong(Objects.requireNonNull(jwt.getSubject()));
    }
}
