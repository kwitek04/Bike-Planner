package pl.pwr.bikeplanner.preferences.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import pl.pwr.bikeplanner.routing.domain.BaseProfile;
import pl.pwr.bikeplanner.routing.domain.SurfacePreference;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "favorite_routes")
public class FavoriteRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "favorite_route_points", joinColumns = @JoinColumn(name = "favorite_route_id"))
    @OrderColumn(name = "position")
    private List<FavoriteRoutePoint> points = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "base_profile", nullable = false)
    private BaseProfile baseProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurfacePreference surface;

    @Column(name = "avoid_main_roads", nullable = false)
    private boolean avoidMainRoads;

    @Column(name = "avoid_steep_climbs", nullable = false)
    private boolean avoidSteepClimbs;

    @Column(name = "distance_m", nullable = false)
    private double distanceMeters;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected FavoriteRoute() {
    }

    public FavoriteRoute(
            Long userId,
            String name,
            List<FavoriteRoutePoint> points,
            BaseProfile baseProfile,
            SurfacePreference surface,
            boolean avoidMainRoads,
            boolean avoidSteepClimbs,
            double distanceMeters) {
        this.userId = userId;
        this.name = name;
        this.points = new ArrayList<>(points);
        this.baseProfile = baseProfile;
        this.surface = surface;
        this.avoidMainRoads = avoidMainRoads;
        this.avoidSteepClimbs = avoidSteepClimbs;
        this.distanceMeters = distanceMeters;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public List<FavoriteRoutePoint> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public BaseProfile getBaseProfile() {
        return baseProfile;
    }

    public SurfacePreference getSurface() {
        return surface;
    }

    public boolean isAvoidMainRoads() {
        return avoidMainRoads;
    }

    public boolean isAvoidSteepClimbs() {
        return avoidSteepClimbs;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
