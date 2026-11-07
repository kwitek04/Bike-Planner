package pl.pwr.bikeplanner.accounts.api;

import pl.pwr.bikeplanner.accounts.domain.User;
import pl.pwr.bikeplanner.security.TokenService.IssuedToken;

import java.time.Instant;

record AuthResponse(String token, Instant expiresAt, AccountResponse account) {

    static AuthResponse of(IssuedToken token, User user) {
        return new AuthResponse(token.value(), token.expiresAt(), AccountResponse.from(user));
    }
}
