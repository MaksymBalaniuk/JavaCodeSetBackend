package com.javacodeset.util;

import com.javacodeset.dto.UserDto;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserResponseCredentialsHidingPolicyTest {

    @Test
    public void hide_shouldReturnUserDtoWithHiddenPersonalData() {
        UserDto userDto = new UserDto();
        userDto.setEmail("maxim@gmail.com");
        userDto.setPassword("12345");
        UserDto expected = new UserDto();
        expected.setEmail(UserResponseCredentialsHidingPolicy.FAKE_EMAIL_VALUE);
        expected.setPassword(UserResponseCredentialsHidingPolicy.FAKE_PASSWORD_VALUE);

        UserDto actual = UserResponseCredentialsHidingPolicy.hide(userDto);

        assertEquals(expected, actual);
    }
}
