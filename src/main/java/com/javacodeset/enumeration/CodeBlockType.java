package com.javacodeset.enumeration;

public enum CodeBlockType {
    PRIVATE(0),
    PUBLIC(1),
    HIDDEN(2);

    public final int type;

    CodeBlockType(int type) {
        this.type = type;
    }
}
