package com.javacodeset.rest;

import com.javacodeset.dto.UserDto;
import com.javacodeset.dto.auth.AuthenticationResponseDto;
import com.javacodeset.dto.premium.PremiumLimitsDto;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.UserPremium;
import com.javacodeset.enumeration.UserStatus;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.repository.AuthorityRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.security.jwt.JwtProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRestControllerIntegrationTest {

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

    private final UserEntity userEntity =
            EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
    private final HttpHeaders headers = new HttpHeaders();
    private RestTemplate patchRestTemplate;

    @Before
    public void setup() {
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setPremium(UserPremium.NONE);
        userRepository.save(userEntity);
        RestControllerIntegrationTestUtils.resetAuthorizationHeader(headers, jwtProvider, jwtPrefix, userEntity);

        patchRestTemplate = restTemplate.getRestTemplate();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @After
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    public void getUserById_validId_shouldReturnUserDto() {
        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/users/get/" + userEntity.getId());

        ResponseEntity<UserDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void getAuthenticatedUser_shouldReturnAuthenticatedUserDto() {
        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/users/authenticated-user/get");

        ResponseEntity<UserDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void getUserByUsername_validUsername_shouldReturnUserDto() {
        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/users/get/by-username/" + userEntity.getUsername());

        ResponseEntity<UserDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void searchUsersByUsername_userEntityExist_shouldReturnUserDtoList() {
        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/users/get-all/by-username/" + userEntity.getUsername());

        ResponseEntity<UserDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), UserDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void updateAuthenticatedUserUsername_validData_shouldReturnAuthenticationResponseDto() {
        String newUsername = "2";

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/users/authenticated-user/update/username/" + newUsername);

        ResponseEntity<AuthenticationResponseDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(headers), AuthenticationResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void updateAuthenticatedUserEmail_validData_shouldReturnAuthenticationResponseDto() {
        String newEmail = "2@gmail.com";

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/users/authenticated-user/update/email/" + newEmail);

        ResponseEntity<AuthenticationResponseDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(headers), AuthenticationResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void updateUserPremiumById_validData_shouldReturnAuthenticationResponseDto() {
        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/users/update/" + userEntity.getId() + "/user-premium/" + UserPremium.UNLIMITED);

        ResponseEntity<AuthenticationResponseDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(headers), AuthenticationResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void activateUserById_validId_shouldActivateUserEntity() {
        RestControllerIntegrationTestUtils.grantAdminAuthority(userEntity, userRepository, authorityRepository);
        RestControllerIntegrationTestUtils.resetAuthorizationHeader(headers, jwtProvider, jwtPrefix, userEntity);

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/users/update/" + userEntity.getId() + "/activate");

        ResponseEntity<UserDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(headers), UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
        assertEquals(UserStatus.ACTIVE, response.getBody().getStatus());

        RestControllerIntegrationTestUtils.removeAdminAuthority(userEntity, userRepository, authorityRepository);
    }

    @Test
    public void banUserById_validId_shouldBanUserEntity() {
        RestControllerIntegrationTestUtils.grantAdminAuthority(userEntity, userRepository, authorityRepository);
        RestControllerIntegrationTestUtils.resetAuthorizationHeader(headers, jwtProvider, jwtPrefix, userEntity);

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/users/update/" + userEntity.getId() + "/ban");

        ResponseEntity<UserDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(headers), UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
        assertEquals(UserStatus.BANNED, response.getBody().getStatus());

        RestControllerIntegrationTestUtils.removeAdminAuthority(userEntity, userRepository, authorityRepository);
    }

    @Test
    public void markUserDeletedById_validId_shouldMarkDeletedUserEntity() {
        RestControllerIntegrationTestUtils.grantDeveloperAuthority(userEntity, userRepository, authorityRepository);
        RestControllerIntegrationTestUtils.resetAuthorizationHeader(headers, jwtProvider, jwtPrefix, userEntity);

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/users/update/" + userEntity.getId() + "/mark-deleted");

        ResponseEntity<UserDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(headers), UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
        assertEquals(UserStatus.DELETED, response.getBody().getStatus());

        RestControllerIntegrationTestUtils.removeDeveloperAuthority(userEntity, userRepository, authorityRepository);
    }

    @Test
    public void getUserPremiumLimits_validId_shouldReturnPremiumLimitsDto() {
        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/users/get/" + userEntity.getId() + "/premium-limits");

        ResponseEntity<PremiumLimitsDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), PremiumLimitsDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    @Test
    public void getPremiumLimitsByUserPremium_validPremium_shouldReturnPremiumLimitsDto() {
        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/users/get/premium-limits/by-user-premium/" + UserPremium.UNLIMITED);

        ResponseEntity<PremiumLimitsDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), PremiumLimitsDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }
}
