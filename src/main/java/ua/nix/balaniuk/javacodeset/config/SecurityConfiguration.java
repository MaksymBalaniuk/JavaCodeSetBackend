package ua.nix.balaniuk.javacodeset.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import ua.nix.balaniuk.javacodeset.security.jwt.JwtConfigurer;
import ua.nix.balaniuk.javacodeset.security.jwt.JwtTokenProvider;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final String[] authWhiteList;
    private final String[] adminAccessList;
    private final String[] developerAccessList;

    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider,
                                 RestAuthenticationEntryPoint authenticationEntryPoint,
                                 @Qualifier("authWhiteList") String[] authWhiteList,
                                 @Qualifier("adminAccessList") String[] adminAccessList,
                                 @Qualifier("developerAccessList") String[] developerAccessList) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authWhiteList = authWhiteList;
        this.adminAccessList = adminAccessList;
        this.developerAccessList = developerAccessList;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(authWhiteList).permitAll()
                        .antMatchers(adminAccessList).hasRole("ADMIN")
                        .antMatchers(developerAccessList).hasRole("DEVELOPER")
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
                .apply(new JwtConfigurer(jwtTokenProvider, authenticationEntryPoint))
                .and()
                .build();
    }
}
