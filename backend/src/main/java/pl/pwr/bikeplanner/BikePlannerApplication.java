package pl.pwr.bikeplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BikePlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BikePlannerApplication.class, args);
    }
}
