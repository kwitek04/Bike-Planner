package pl.pwr.bikeplanner.routing.infrastructure.graphhopper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "routing.graphhopper")
public record GraphHopperProperties(
        Path osmFile,
        Path graphLocation,
        @DefaultValue Elevation elevation) {

    public record Elevation(
            @DefaultValue("false") boolean enabled,
            @DefaultValue("data/srtm") Path cacheDir) {
    }
}
