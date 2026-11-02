package pl.pwr.bikeplanner.routing.infrastructure.graphhopper;

import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;

@Configuration
class GraphHopperConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GraphHopperConfiguration.class);

    @Bean(destroyMethod = "close")
    GraphHopper graphHopper(GraphHopperProperties properties) {
        if (!Files.isRegularFile(properties.osmFile())) {
            throw new IllegalStateException("OSM file not found: " + properties.osmFile().toAbsolutePath());
        }
        boolean elevation = properties.elevation().enabled();

        GraphHopperConfig config = new GraphHopperConfig();
        config.putObject("datareader.file", properties.osmFile().toString());
        config.putObject("graph.location", properties.graphLocation().toString());
        config.putObject("graph.encoded_values", GraphHopperProfiles.encodedValues(elevation));
        config.putObject("import.osm.ignored_highways", "motorway");
        if (elevation) {
            config.putObject("graph.elevation.provider", "srtm");
            config.putObject("graph.elevation.cache_dir", properties.elevation().cacheDir().toString());
        }
        config.setProfiles(GraphHopperProfiles.profiles(elevation));
        config.setLMProfiles(GraphHopperProfiles.landmarkProfiles());

        log.info("Loading routing graph from {} (import of {} if missing)",
                properties.graphLocation(), properties.osmFile());
        GraphHopper hopper = new GraphHopper().init(config);
        hopper.importOrLoad();
        return hopper;
    }

    @Bean
    GraphHopperRoutingEngine graphHopperRoutingEngine(GraphHopper hopper, GraphHopperProperties properties) {
        return new GraphHopperRoutingEngine(hopper, new CustomModelFactory(properties.elevation().enabled()));
    }
}
