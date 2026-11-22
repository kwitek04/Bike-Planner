package pl.pwr.bikeplanner.preferences.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRouteRepository extends JpaRepository<FavoriteRoute, Long> {

    @EntityGraph(attributePaths = "points")
    List<FavoriteRoute> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<FavoriteRoute> findByIdAndUserId(Long id, Long userId);
}
