package pl.pwr.bikeplanner.accounts.api;

import java.nio.charset.StandardCharsets;

final class PasswordLimits {

    static final int MAX_BYTES = 72;

    private PasswordLimits() {
    }

    static boolean fits(String password) {
        return password == null || password.getBytes(StandardCharsets.UTF_8).length <= MAX_BYTES;
    }
}
