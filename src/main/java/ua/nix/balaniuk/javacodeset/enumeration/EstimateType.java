package ua.nix.balaniuk.javacodeset.enumeration;

public enum EstimateType {
    LIKE(0),
    DISLIKE(1);

    public final int type;

    EstimateType(int type) {
        this.type = type;
    }
}
