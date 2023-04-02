package com.javacodeset.enumeration;

public enum UserStatus {
    ACTIVE(0),
    BANNED(1),
    DELETED(2);

    public final int status;

    UserStatus(int status) {
        this.status = status;
    }
}
