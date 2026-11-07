package pl.pwr.bikeplanner.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.util.ReflectionTestUtils;
import pl.pwr.bikeplanner.accounts.domain.Role;
import pl.pwr.bikeplanner.accounts.domain.User;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenServiceTest {

    private static final JwtProperties PROPERTIES =
            new JwtProperties("test-secret-with-at-least-32-bytes!!", Duration.ofHours(8));

    private final SecurityConfiguration configuration = new SecurityConfiguration();
    private final JwtDecoder decoder = configuration.jwtDecoder(PROPERTIES);
    
    private static final String EMAIL = "email@example.com";

    @Test
    void issuedTokenCarriesUserIdEmailAndRole() {
        TokenService service = new TokenService(configuration.jwtEncoder(PROPERTIES), PROPERTIES);

        Jwt jwt = decoder.decode(service.issue(createUser(7L, Role.MODERATOR)).value());

        assertThat(jwt.getSubject()).isEqualTo("7");
        assertThat(jwt.getClaimAsString("email")).isEqualTo(EMAIL);
        assertThat(jwt.getClaimAsString("role")).isEqualTo("MODERATOR");
        assertThat(jwt.getClaimAsString("iss")).isEqualTo(TokenService.ISSUER);
    }

    @Test
    void tokenExpiresAfterConfiguredTtl() {
        Instant now = Instant.parse("2026-11-07T12:00:00Z");
        TokenService service = new TokenService(
                configuration.jwtEncoder(PROPERTIES), PROPERTIES, Clock.fixed(now, ZoneOffset.UTC));

        assertThat(service.issue(createUser(1L, Role.CYCLIST)).expiresAt()).isEqualTo(now.plus(Duration.ofHours(8)));
    }

    @Test
    void tokenSignedWithDifferentSecretIsRejected() {
        JwtProperties other = new JwtProperties("another-secret-with-at-least-32-bytes", Duration.ofHours(8));
        TokenService service = new TokenService(configuration.jwtEncoder(other), other);
        String token = service.issue(createUser(1L, Role.CYCLIST)).value();

        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void tooShortSecretIsRejected() {
        JwtProperties weak = new JwtProperties("short", Duration.ofHours(8));

        assertThatThrownBy(() -> SecurityConfiguration.signingKey(weak))
                .isInstanceOf(IllegalStateException.class);
    }

    private static User createUser(long id, Role role) {
        User user = new User(EMAIL, "hash", role);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
