package pl.pwr.bikeplanner.accounts.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pwr.bikeplanner.accounts.domain.Role;
import pl.pwr.bikeplanner.accounts.domain.User;
import pl.pwr.bikeplanner.accounts.domain.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    private final UserRepository users = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);
    private final AccountService service = new AccountService(users, passwordEncoder);
    
    private static final String EMAIL = "email@example.com";
    private static final String PASSWORD = "testpassword";

    @BeforeEach
    void saveReturnsArgument() {
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void registerStoresHashedPasswordAndCyclistRole() {
        User user = service.register("  Email@Example.com ", PASSWORD);

        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getRole()).isEqualTo(Role.CYCLIST);
        assertThat(user.getPasswordHash()).isNotEqualTo(PASSWORD);
        assertThat(passwordEncoder.matches(PASSWORD, user.getPasswordHash())).isTrue();
    }

    @Test
    void registerRejectsEmailAlreadyInUse() {
        when(users.existsByEmail(EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> service.register(EMAIL, PASSWORD))
                .isInstanceOf(EmailAlreadyUsedException.class);
        verify(users, never()).save(any());
    }

    @Test
    void authenticateAcceptsCorrectPassword() {
        User stored = new User(EMAIL, passwordEncoder.encode(PASSWORD), Role.CYCLIST);
        when(users.findByEmail(EMAIL)).thenReturn(Optional.of(stored));

        assertThat(service.authenticate("EMAIL@example.com", PASSWORD)).isSameAs(stored);
    }

    @Test
    void authenticateRejectsWrongPassword() {
        User stored = new User(EMAIL, passwordEncoder.encode(PASSWORD), Role.CYCLIST);
        when(users.findByEmail(EMAIL)).thenReturn(Optional.of(stored));

        assertThatThrownBy(() -> service.authenticate(EMAIL, "wrong_password"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void authenticateRejectsUnknownEmail() {
        when(users.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.authenticate("unknown@example.com", PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
