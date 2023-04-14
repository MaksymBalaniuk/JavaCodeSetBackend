package com.javacodeset.security.jwt;

import com.javacodeset.security.userdetails.JwtUserDetailsService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.javacodeset.entity.AuthorityEntity;
import com.javacodeset.exception.JwtAuthenticationException;
import com.javacodeset.util.AuthorityUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.token.prefix}")
    private String prefix;

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;

    private final JwtUserDetailsService jwtUserDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String username, Set<AuthorityEntity> authorities) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("authorities", AuthorityUtils.mapToStringList(authorities));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails;
        try {
            userDetails = jwtUserDetailsService.loadUserByUsername(getUsernameFromToken(token));
        } catch (UsernameNotFoundException ex) {
            throw new JwtAuthenticationException("JWT is invalid, user not found");
        }
        if (!validateAuthorities(userDetails, token))
            throw new JwtAuthenticationException("JWT is invalid, user access rights are incorrect");
        if (!userDetails.isAccountNonLocked())
            throw new JwtAuthenticationException("JWT is invalid, user is locked");
        if (!userDetails.isEnabled())
            throw new JwtAuthenticationException("JWT is invalid, user is disabled");
        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (Objects.nonNull(bearerToken) && bearerToken.startsWith(prefix))
            return bearerToken.substring(prefix.length());
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        } catch (ExpiredJwtException ex) {
            throw new JwtAuthenticationException("JWT is expired");
        } catch (JwtException | IllegalArgumentException ex) {
            throw new JwtAuthenticationException("JWT is invalid");
        }
        return true;
    }

    private boolean validateAuthorities(UserDetails userDetails, String token) {
        List<String> userDetailsAuthorities = AuthorityUtils.mapToStringList(userDetails.getAuthorities());
        List<String> tokenAuthorities = getAuthoritiesFromToken(token);
        if (Objects.isNull(tokenAuthorities))
            return false;
        return userDetailsAuthorities.stream().sorted().toList()
                .equals(tokenAuthorities.stream().sorted().toList());
    }

    private String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    private List<String> getAuthoritiesFromToken(String token) {
        Object authList = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().get("authorities");
        if (authList instanceof List<?> authorities)
            return authorities.stream().map(object -> (String) object).toList();
        return null;
    }
}
