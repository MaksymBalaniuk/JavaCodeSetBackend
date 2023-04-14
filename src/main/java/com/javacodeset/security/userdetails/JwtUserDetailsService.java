package com.javacodeset.security.userdetails;

import com.javacodeset.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface JwtUserDetailsService extends UserDetailsService {
    UserEntity getAuthenticatedUser();
}
