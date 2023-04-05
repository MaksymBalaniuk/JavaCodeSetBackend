package com.javacodeset.security.userdetails;

import org.springframework.security.core.GrantedAuthority;
import com.javacodeset.dto.UserDto;
import com.javacodeset.enumeration.UserStatus;

import java.util.Collection;
import java.util.Objects;

public final class JwtUserFactory {

    public static JwtUser create(UserDto user, Collection<? extends GrantedAuthority> authorities) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPremium(),
                isUserEnabled(user),
                isUserLocked(user),
                authorities
        );
    }

    private static boolean isUserEnabled(UserDto user) {
        return Objects.equals(user.getStatus(), UserStatus.ACTIVE);
    }

    private static boolean isUserLocked(UserDto user) {
        return Objects.equals(user.getStatus(), UserStatus.BANNED) ||
                Objects.equals(user.getStatus(), UserStatus.DELETED);
    }
}
