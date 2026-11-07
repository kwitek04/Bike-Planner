package pl.pwr.bikeplanner.accounts.api;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record RegisterRequest(
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8) String password) {

    @AssertTrue(message = "password must not exceed " + PasswordLimits.MAX_BYTES + " bytes")
    boolean isPasswordWithinLimit() {
        return PasswordLimits.fits(password);
    }
}
