package pl.pwr.bikeplanner.accounts.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pwr.bikeplanner.accounts.domain.Role;
import pl.pwr.bikeplanner.accounts.domain.User;
import pl.pwr.bikeplanner.accounts.domain.UserRepository;

import java.util.Locale;
import java.util.Optional;

@Service
public class AccountService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AccountService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        if (users.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyUsedException(normalizedEmail);
        }
        return users.save(new User(normalizedEmail, passwordEncoder.encode(password), Role.CYCLIST));
    }

    @Transactional(readOnly = true)
    public User authenticate(String email, String password) {
        return users.findByEmail(normalizeEmail(email))
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> findAccount(long id) {
        return users.findById(id);
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
