package pl.pwr.bikeplanner.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "accounts.jwt")
public record JwtProperties(String secret, Duration ttl) {
}
