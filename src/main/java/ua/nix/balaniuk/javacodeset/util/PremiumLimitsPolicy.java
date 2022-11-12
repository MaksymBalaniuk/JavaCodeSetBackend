package ua.nix.balaniuk.javacodeset.util;

import ua.nix.balaniuk.javacodeset.dto.premium.PremiumLimitsDto;
import ua.nix.balaniuk.javacodeset.enumeration.UserPremium;
import ua.nix.balaniuk.javacodeset.exception.NotFoundException;

import java.util.Objects;

public abstract class PremiumLimitsPolicy {

    private PremiumLimitsPolicy() {
    }

    public static PremiumLimitsDto getPremiumLimits(UserPremium premium) {
        if (Objects.equals(premium, UserPremium.NONE))
            return new PremiumLimitsDto(10, 1020, 1020);
        else if (Objects.equals(premium, UserPremium.ORDINARY))
            return new PremiumLimitsDto(30, 2040, 2040);
        else if (Objects.equals(premium, UserPremium.UNLIMITED))
            return new PremiumLimitsDto(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

        throw new NotFoundException(String.format("User premium value '%s' not found in policy", premium));
    }
}
