package com.javacodeset.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.javacodeset.entity.AuthorityEntity;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class AuthorityUtils {

    private AuthorityUtils() {
    }

    public static Collection<? extends GrantedAuthority> mapToGrantedAuthorities(Set<AuthorityEntity> authorities) {
        return authorities.stream().map(a -> new SimpleGrantedAuthority(a.getName())).toList();
    }

    public static List<String> mapToStringList(Set<AuthorityEntity> authorities) {
        return authorities.stream().map(AuthorityEntity::getName).toList();
    }

    public static List<String> mapToStringList(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority).toList();
    }
}
