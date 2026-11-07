package pl.pwr.bikeplanner.accounts.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.pwr.bikeplanner.accounts.application.AccountService;
import pl.pwr.bikeplanner.accounts.domain.User;
import pl.pwr.bikeplanner.security.TokenService;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    private final AccountService accountService;
    private final TokenService tokenService;

    AuthController(AccountService accountService, TokenService tokenService) {
        this.accountService = accountService;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = accountService.register(request.email(), request.password());
        return AuthResponse.of(tokenService.issue(user), user);
    }

    @PostMapping("/login")
    AuthResponse login(@Valid @RequestBody LoginRequest request) {
        User user = accountService.authenticate(request.email(), request.password());
        return AuthResponse.of(tokenService.issue(user), user);
    }

    @GetMapping("/me")
    AccountResponse me(@AuthenticationPrincipal Jwt jwt) {
        return accountService.findAccount(Long.parseLong(Objects.requireNonNull(jwt.getSubject())))
                .map(AccountResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account no longer exists"));
    }
}
