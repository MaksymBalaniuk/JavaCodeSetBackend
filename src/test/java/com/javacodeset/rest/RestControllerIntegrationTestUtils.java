package com.javacodeset.rest;

import com.javacodeset.entity.AuthorityEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.repository.AuthorityRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

public final class RestControllerIntegrationTestUtils {

    public static String buildEndpointPath(int port, String endpoint) {
        return "http://localhost:" + port + endpoint;
    }

    public static void resetAuthorizationHeader(
            HttpHeaders headers, JwtProvider jwtProvider, String jwtPrefix, UserEntity userEntity) {
        headers.remove("Authorization");
        headers.add("Authorization", jwtPrefix +
                jwtProvider.createToken(userEntity.getUsername(), userEntity.getAuthorities()));
    }

    public static void grantAdminAuthority(
            UserEntity userEntity, UserRepository userRepository, AuthorityRepository authorityRepository) {
        AuthorityEntity databaseAdminAuthority =
                authorityRepository.findByName("ROLE_ADMIN").orElseThrow();

        AuthorityEntity adminAuthority = new AuthorityEntity();
        adminAuthority.setId(databaseAdminAuthority.getId());
        adminAuthority.setName(databaseAdminAuthority.getName());

        userEntity.getAuthorities().add(adminAuthority);
        adminAuthority.getUsers().add(userEntity);
        userRepository.save(userEntity);
        authorityRepository.save(adminAuthority);
    }

    public static void removeAdminAuthority(
            UserEntity userEntity, UserRepository userRepository, AuthorityRepository authorityRepository) {
        AuthorityEntity databaseAdminAuthority =
                authorityRepository.findByName("ROLE_ADMIN").orElseThrow();

        AuthorityEntity adminAuthority = new AuthorityEntity();
        adminAuthority.setId(databaseAdminAuthority.getId());
        adminAuthority.setName(databaseAdminAuthority.getName());

        userEntity.getAuthorities().remove(adminAuthority);
        adminAuthority.getUsers().remove(userEntity);
        userRepository.save(userEntity);
        authorityRepository.save(adminAuthority);
    }
}
