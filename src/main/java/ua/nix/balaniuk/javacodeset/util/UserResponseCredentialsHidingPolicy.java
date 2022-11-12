package ua.nix.balaniuk.javacodeset.util;

import ua.nix.balaniuk.javacodeset.dto.UserDto;

public abstract class UserResponseCredentialsHidingPolicy {

    private UserResponseCredentialsHidingPolicy() {
    }

    public static final String FAKE_PASSWORD_VALUE = null;
    public static final String FAKE_EMAIL_VALUE = null;

    public static UserDto hide(UserDto userDto) {
        userDto.setPassword(FAKE_PASSWORD_VALUE);
        userDto.setEmail(FAKE_EMAIL_VALUE);
        return userDto;
    }
}
