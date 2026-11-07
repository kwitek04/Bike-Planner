package pl.pwr.bikeplanner.accounts.api;

import pl.pwr.bikeplanner.accounts.domain.Role;
import pl.pwr.bikeplanner.accounts.domain.User;

record AccountResponse(Long id, String email, Role role) {

    static AccountResponse from(User user) {
        return new AccountResponse(user.getId(), user.getEmail(), user.getRole());
    }
}
