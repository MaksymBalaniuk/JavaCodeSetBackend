package com.javacodeset.util;

import com.javacodeset.enumeration.UserPremium;
import com.javacodeset.exception.NotFoundException;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PremiumLimitsPolicyTest {

    @Test
    public void getPremiumLimits_validPremium_shouldReturnPremiumLimitsDto() {
        assertNotNull(PremiumLimitsPolicy.getPremiumLimits(UserPremium.NONE));
        assertNotNull(PremiumLimitsPolicy.getPremiumLimits(UserPremium.ORDINARY));
        assertNotNull(PremiumLimitsPolicy.getPremiumLimits(UserPremium.UNLIMITED));
    }

    @Test(expected = NotFoundException.class)
    public void getPremiumLimits_invalidPremium_shouldThrowException() {
        PremiumLimitsPolicy.getPremiumLimits(null);
    }
}
