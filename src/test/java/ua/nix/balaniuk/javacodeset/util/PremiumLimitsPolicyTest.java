package ua.nix.balaniuk.javacodeset.util;

import org.junit.Test;
import ua.nix.balaniuk.javacodeset.enumeration.UserPremium;
import ua.nix.balaniuk.javacodeset.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PremiumLimitsPolicyTest {

    @Test
    public void getPremiumLimits_validPremium_shodReturnPremiumLimitsDto() {
        assertNotNull(PremiumLimitsPolicy.getPremiumLimits(UserPremium.NONE));
        assertNotNull(PremiumLimitsPolicy.getPremiumLimits(UserPremium.ORDINARY));
        assertNotNull(PremiumLimitsPolicy.getPremiumLimits(UserPremium.UNLIMITED));
    }

    @Test(expected = NotFoundException.class)
    public void getPremiumLimits_invalidPremium_shodThrowException() {
        PremiumLimitsPolicy.getPremiumLimits(null);
    }
}
