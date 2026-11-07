package pl.pwr.bikeplanner.accounts.api;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

record LoginRequest(@NotBlank String email, @NotBlank String password) {

    @AssertTrue(message = "password must not exceed " + PasswordLimits.MAX_BYTES + " bytes")
    boolean isPasswordWithinLimit() {
        return PasswordLimits.fits(password);
    }
}
