package com.javacodeset.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminAccessListConfiguration {

    @Bean
    @Qualifier("adminAccessList")
    public String[] getAdminAccessList() {
        return new String[] {
                "/api/users/update/{userId}/activate",
                "/api/users/update/{userId}/ban"
        };
    }
}
