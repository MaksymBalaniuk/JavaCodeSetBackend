package ua.nix.balaniuk.javacodeset.rest;

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
import ua.nix.balaniuk.javacodeset.dto.UserDto;
import ua.nix.balaniuk.javacodeset.dto.auth.AuthenticationResponseDto;
import ua.nix.balaniuk.javacodeset.dto.premium.PremiumLimitsDto;
import ua.nix.balaniuk.javacodeset.entity.AuthorityEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.UserPremium;
import ua.nix.balaniuk.javacodeset.enumeration.UserStatus;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;
import ua.nix.balaniuk.javacodeset.repository.AuthorityRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.security.jwt.JwtTokenProvider;

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
    private JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.token.prefix}")
    private String prefix;

    private UserEntity userEntity = EntityExampleStorage.getUserEntity();
    private final HttpHeaders headers = new HttpHeaders();
    private RestTemplate patchRestTemplate;

    @Before
    public void setup() {
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setPremium(UserPremium.UNLIMITED);
        userRepository.save(userEntity);
        grantAdminAuthority();
        grantDeveloperAuthority();

        String token = prefix + jwtTokenProvider
                .createToken(userEntity.getUsername(), userEntity.getAuthorities());
        headers.add("Authorization", token);

        patchRestTemplate = restTemplate.getRestTemplate();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @After
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    public void getUserById_validId_shodReturnTagDto() {
        String endpoint = buildEndpointPath("/api/users/get/" + userEntity.getId());

        ResponseEntity<UserDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void updateUsernameById_validUsername_shodReturnUpdatedUserDto() {
        String newUsername = "artur";
        String endpoint = buildEndpointPath("/api/users/update/" + userEntity.getId() + "/username/" + newUsername);

        ResponseEntity<AuthenticationResponseDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(headers), AuthenticationResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void activateUserById_validId_shodActivateUserEntity() {
        String endpoint = buildEndpointPath("/api/users/update/" + userEntity.getId() + "/activate");

        ResponseEntity<UserDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(headers), UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
        assertEquals(UserStatus.ACTIVE, response.getBody().getStatus());
    }

    @Test
    public void banUserById_validId_shodBanUserEntity() {
        String endpoint = buildEndpointPath("/api/users/update/" + userEntity.getId() + "/ban");

        ResponseEntity<UserDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(headers), UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
        assertEquals(UserStatus.BANNED, response.getBody().getStatus());
    }

    @Test
    public void markUserDeletedById_validId_shodMarkDeletedUserEntity() {
        String endpoint = buildEndpointPath("/api/users/update/" + userEntity.getId() + "/mark-deleted");

        ResponseEntity<UserDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(headers), UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
        assertEquals(UserStatus.DELETED, response.getBody().getStatus());
    }

    @Test
    public void getUserPremiumLimits_validId_shodReturnPremiumLimitsDto() {
        String endpoint = buildEndpointPath("/api/users/get/" + userEntity.getId() + "/premium-limits");

        ResponseEntity<PremiumLimitsDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), PremiumLimitsDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    @Test
    public void getPremiumLimitsByUserPremium_validPremium_shodReturnPremiumLimitsDto() {
        String endpoint = buildEndpointPath("/api/users/get/premium-limits/by-user-premium/" + UserPremium.UNLIMITED);

        ResponseEntity<PremiumLimitsDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), PremiumLimitsDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    private void grantAdminAuthority() {
        AuthorityEntity databaseAdminAuthority =
                authorityRepository.findByName("ROLE_ADMIN").orElseThrow();

        AuthorityEntity adminAuthority = new AuthorityEntity();
        adminAuthority.setId(databaseAdminAuthority.getId());
        adminAuthority.setName(databaseAdminAuthority.getName());

        userEntity.getAuthorities().add(adminAuthority);
        adminAuthority.getUsers().add(userEntity);
        userEntity = userRepository.save(userEntity);
        authorityRepository.save(adminAuthority);
    }

    private void grantDeveloperAuthority() {
        AuthorityEntity databaseDeveloperAuthority =
                authorityRepository.findByName("ROLE_DEVELOPER").orElseThrow();

        AuthorityEntity developerAuthority = new AuthorityEntity();
        developerAuthority.setId(databaseDeveloperAuthority.getId());
        developerAuthority.setName(databaseDeveloperAuthority.getName());

        userEntity.getAuthorities().add(developerAuthority);
        developerAuthority.getUsers().add(userEntity);
        userEntity = userRepository.save(userEntity);
        authorityRepository.save(developerAuthority);
    }

    private String buildEndpointPath(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }
}
