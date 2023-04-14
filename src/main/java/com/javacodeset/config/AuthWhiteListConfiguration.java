package com.javacodeset.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthWhiteListConfiguration {

    @Bean
    @Qualifier("authWhiteList")
    public String[] getAuthWhiteList() {
        return new String[] {
                "/api/auth/login",
                "/api/auth/register",
                "/api/users/get/**",
                "/api/users/get-all/by-username/**",
                "/api/tags/get/**",
                "/api/tags/get-all/by-block-id/**",
                "/api/blocks/get-all/filtered",
                "/api/estimates/get-all/by-block-id/**",
                "/api/comments/get-all/by-block-id/**",
                "/swagger-resources",
                "/documentation/swagger-ui",
                "/swagger-resources/**",
                "/swagger-resources/configuration/ui",
                "/swagger-resources/configuration/security",
                "/swagger-ui",
                "/swagger-ui/index.html",
                "/swagger-ui/**",
                "/favicon.ico",
                "/error",
                "/webjars/**",
                "/v2/api-docs",
                "/swagger-ui.html",
                "/api/swagger-ui.html"
        };
    }
}
