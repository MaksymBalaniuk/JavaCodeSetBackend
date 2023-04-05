package com.javacodeset.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeveloperAccessListConfiguration {

    @Bean
    @Qualifier("developerAccessList")
    public String[] getDeveloperAccessList() {
        return new String[] {
                "/api/executor/enable",
                "/api/executor/disable",
                "/api/users/update/{userId}/mark-deleted"
        };
    }
}
