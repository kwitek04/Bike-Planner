CREATE TABLE favorite_routes (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id            BIGINT           NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name               VARCHAR(255)     NOT NULL,
    base_profile       VARCHAR(20)      NOT NULL CHECK (base_profile IN ('ROAD', 'GRAVEL', 'MTB')),
    surface            VARCHAR(20)      NOT NULL CHECK (surface IN ('NO_PREFERENCE', 'PREFER_PAVED', 'PREFER_UNPAVED')),
    avoid_main_roads   BOOLEAN          NOT NULL,
    avoid_steep_climbs BOOLEAN          NOT NULL,
    distance_m         DOUBLE PRECISION NOT NULL CHECK (distance_m >= 0),
    created_at         TIMESTAMPTZ      NOT NULL DEFAULT now()
);

CREATE INDEX favorite_routes_user_id_idx ON favorite_routes (user_id);

CREATE TABLE favorite_route_points (
    favorite_route_id BIGINT           NOT NULL REFERENCES favorite_routes (id) ON DELETE CASCADE,
    position          INTEGER          NOT NULL CHECK (position >= 0),
    lat               DOUBLE PRECISION NOT NULL CHECK (lat BETWEEN -90 AND 90),
    lon               DOUBLE PRECISION NOT NULL CHECK (lon BETWEEN -180 AND 180),
    PRIMARY KEY (favorite_route_id, position)
);
