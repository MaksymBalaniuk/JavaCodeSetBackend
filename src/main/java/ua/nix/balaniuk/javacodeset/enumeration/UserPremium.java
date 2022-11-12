package ua.nix.balaniuk.javacodeset.enumeration;

public enum UserPremium {
    NONE(0),
    ORDINARY(1),
    UNLIMITED(2);

    public final int premium;

    UserPremium(int premium) {
        this.premium = premium;
    }
}
