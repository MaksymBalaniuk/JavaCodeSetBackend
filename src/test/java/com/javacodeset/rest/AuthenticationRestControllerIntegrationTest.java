package com.javacodeset.rest;

import com.javacodeset.dto.auth.AuthenticationRequestDto;
import com.javacodeset.dto.auth.AuthenticationResponseDto;
import com.javacodeset.dto.auth.RegisterRequestDto;
import com.javacodeset.dto.auth.RegisterResponseDto;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.repository.UserRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void login_validData_shouldReturnAuthenticationResponseDto() {
        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/auth/login");
        AuthenticationRequestDto body = DtoExampleStorage.getAuthenticationRequestDto("1", "1");

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(body.getUsername());
        userEntity.setPassword(passwordEncoder.encode(body.getPassword()));
        userEntity.setEmail("1@gmail.com");
        userRepository.save(userEntity);

        ResponseEntity<AuthenticationResponseDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body), AuthenticationResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    @Test
    public void register_validData_shouldReturnRegisterResponseDto() {
        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/auth/register");
        RegisterRequestDto body = DtoExampleStorage.getRegisterRequestDto("1", "1", "1@gmail.com");

        ResponseEntity<RegisterResponseDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body), RegisterResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }
}
