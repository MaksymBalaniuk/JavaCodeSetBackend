package ua.nix.balaniuk.javacodeset.rest;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ua.nix.balaniuk.javacodeset.dto.auth.AuthenticationRequestDto;
import ua.nix.balaniuk.javacodeset.dto.auth.AuthenticationResponseDto;
import ua.nix.balaniuk.javacodeset.dto.auth.RegisterRequestDto;
import ua.nix.balaniuk.javacodeset.dto.auth.RegisterResponseDto;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.example.DtoExampleStorage;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationRestControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @After
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    public void login_validData_shodReturnAuthenticationResponseDto() {
        String endpoint = buildEndpointPath("/api/auth/login");
        AuthenticationRequestDto body = DtoExampleStorage.getAuthenticationRequestDto();

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(body.getUsername());
        userEntity.setPassword(passwordEncoder.encode(body.getPassword()));
        userEntity.setEmail("email");
        userRepository.save(userEntity);

        ResponseEntity<AuthenticationResponseDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body), AuthenticationResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    @Test
    public void register_validData_shodReturnRegisterResponseDto() {
        String endpoint = buildEndpointPath("/api/auth/register");
        RegisterRequestDto body = DtoExampleStorage.getRegisterRequestDto();

        ResponseEntity<RegisterResponseDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body), RegisterResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    private String buildEndpointPath(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }
}
