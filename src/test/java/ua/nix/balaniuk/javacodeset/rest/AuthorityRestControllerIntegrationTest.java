package ua.nix.balaniuk.javacodeset.rest;

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
import ua.nix.balaniuk.javacodeset.dto.permission.UserPermissionsDto;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.UserStatus;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.security.jwt.JwtTokenProvider;

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
    private JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.token.prefix}")
    private String prefix;

    private UserEntity userEntity = EntityExampleStorage.getUserEntity();
    private final HttpHeaders headers = new HttpHeaders();

    @Before
    public void setup() {
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity = userRepository.save(userEntity);
        String token = prefix + jwtTokenProvider
                .createToken(userEntity.getUsername(), userEntity.getAuthorities());
        headers.add("Authorization", token);
    }

    @After
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    public void getUserPermissions_noAuthorities_shodReturnUserPermissionsDto() {
        String endpoint = buildEndpointPath("/api/authorities/get/" + userEntity.getId() + "/permissions");

        ResponseEntity<UserPermissionsDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), UserPermissionsDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    private String buildEndpointPath(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }
}
