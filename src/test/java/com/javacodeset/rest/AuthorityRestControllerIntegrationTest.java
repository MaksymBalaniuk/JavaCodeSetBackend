package com.javacodeset.rest;

import com.javacodeset.dto.permission.UserPermissionsDto;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.UserStatus;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.repository.AuthorityRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.security.jwt.JwtProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorityRestControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Value("${jwt.token.prefix}")
    private String jwtPrefix;

    private UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
    private final HttpHeaders headers = new HttpHeaders();

    @Before
    public void setup() {
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity = userRepository.save(userEntity);
        RestControllerIntegrationTestUtils.resetAuthorizationHeader(headers, jwtProvider, jwtPrefix, userEntity);
    }

    @After
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    public void getUserPermissions_noAuthorities_shouldReturnUserPermissionsDto() {
        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/authorities/get/" + userEntity.getId() + "/permissions");

        ResponseEntity<UserPermissionsDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), UserPermissionsDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    @Test
    public void isUserHasAdminAuthority_validData_shouldReturnBoolean() {
        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/authorities/get/" + userEntity.getId() + "/is-admin");

        ResponseEntity<Boolean> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isFalse();
    }

    @Test
    public void addAdminAuthorityToUser_validData_shouldAddAuthorityEntityToUserEntity() {
        UserEntity targetUserEntity = userRepository.save(
                EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com"));
        RestControllerIntegrationTestUtils.grantAdminAuthority(userEntity, userRepository, authorityRepository);
        RestControllerIntegrationTestUtils.resetAuthorizationHeader(headers, jwtProvider, jwtPrefix, userEntity);

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/authorities/add/authority-to-user/admin/" + targetUserEntity.getId());

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        RestControllerIntegrationTestUtils.removeAdminAuthority(userEntity, userRepository, authorityRepository);
    }
}
