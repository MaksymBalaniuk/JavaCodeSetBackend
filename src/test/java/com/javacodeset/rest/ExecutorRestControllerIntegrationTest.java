package com.javacodeset.rest;

import com.javacodeset.dto.executor.JavaCodeExecutionRequestDto;
import com.javacodeset.dto.executor.JavaCodeExecutionResponseDto;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.UserStatus;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.repository.AuthorityRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.security.jwt.JwtProvider;
import com.javacodeset.tool.JavaCodeExecutor;
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
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExecutorRestControllerIntegrationTest {

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

    @Autowired
    private JavaCodeExecutor javaCodeExecutor;

    @Value("${jwt.token.prefix}")
    private String jwtPrefix;

    private final UserEntity userEntity =
            EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
    private final HttpHeaders headers = new HttpHeaders();

    @Before
    public void setup() {
        userEntity.setStatus(UserStatus.ACTIVE);
        userRepository.save(userEntity);
        RestControllerIntegrationTestUtils.resetAuthorizationHeader(headers, jwtProvider, jwtPrefix, userEntity);
        javaCodeExecutor.enable();
    }

    @After
    public void clear() {
        userRepository.deleteAll();
        javaCodeExecutor.enable();
    }

    @Test
    public void isExecutorEnabled_shouldReturnBoolean() {
        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/executor/is-enabled");
        ResponseEntity<Boolean> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isTrue();
    }

    @Test
    public void enableExecutor_shouldEnableExecutor() {
        RestControllerIntegrationTestUtils.grantDeveloperAuthority(userEntity, userRepository, authorityRepository);
        RestControllerIntegrationTestUtils.resetAuthorizationHeader(headers, jwtProvider, jwtPrefix, userEntity);

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/executor/enable");

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(javaCodeExecutor.isEnabled());

        RestControllerIntegrationTestUtils.removeDeveloperAuthority(userEntity, userRepository, authorityRepository);
    }

    @Test
    public void disableExecutor_shouldDisableExecutor() {
        RestControllerIntegrationTestUtils.grantDeveloperAuthority(userEntity, userRepository, authorityRepository);
        RestControllerIntegrationTestUtils.resetAuthorizationHeader(headers, jwtProvider, jwtPrefix, userEntity);

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/executor/disable");

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(javaCodeExecutor.isEnabled());

        RestControllerIntegrationTestUtils.removeDeveloperAuthority(userEntity, userRepository, authorityRepository);
    }

    @Test
    public void execute_validData_shouldReturnJavaCodeExecutionResponseDto() {
        JavaCodeExecutionRequestDto body = DtoExampleStorage.getJavaCodeExecutionRequestDto();

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/executor/execute");

        ResponseEntity<JavaCodeExecutionResponseDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), JavaCodeExecutionResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertThat(response.getBody().getExitCode()).isEqualTo(0);
    }
}
