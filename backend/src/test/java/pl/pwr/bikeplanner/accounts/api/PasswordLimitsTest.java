package pl.pwr.bikeplanner.accounts.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordLimitsTest {

    @Test
    void limitIsCountedInUtf8BytesNotCharacters() {
        assertThat(PasswordLimits.fits("ż".repeat(36))).isTrue();
        assertThat(PasswordLimits.fits("ż".repeat(37))).isFalse();
        assertThat(PasswordLimits.fits("a".repeat(72))).isTrue();
    }
}
