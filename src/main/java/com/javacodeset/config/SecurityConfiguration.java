package com.javacodeset.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import com.javacodeset.security.jwt.JwtConfigurer;
import com.javacodeset.security.jwt.JwtProvider;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtProvider jwtProvider;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final String[] authWhiteList;
    private final String[] adminAccessList;

    public SecurityConfiguration(JwtProvider jwtProvider,
                                 RestAuthenticationEntryPoint authenticationEntryPoint,
                                 @Qualifier("authWhiteList") String[] authWhiteList,
                                 @Qualifier("adminAccessList") String[] adminAccessList) {
        this.jwtProvider = jwtProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authWhiteList = authWhiteList;
        this.adminAccessList = adminAccessList;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(authWhiteList).permitAll()
                        .antMatchers(adminAccessList).hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .cors()
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
                .and()
                .apply(new JwtConfigurer(jwtProvider, authenticationEntryPoint))
                .and()
                .build();
    }
}
