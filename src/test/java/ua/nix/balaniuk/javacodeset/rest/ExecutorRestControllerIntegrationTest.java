package ua.nix.balaniuk.javacodeset.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ua.nix.balaniuk.javacodeset.dto.executor.JavaCodeExecutionRequestDto;
import ua.nix.balaniuk.javacodeset.dto.executor.JavaCodeExecutionResponseDto;
import ua.nix.balaniuk.javacodeset.entity.AuthorityEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.UserStatus;
import ua.nix.balaniuk.javacodeset.example.DtoExampleStorage;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;
import ua.nix.balaniuk.javacodeset.repository.AuthorityRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.security.jwt.JwtTokenProvider;
import ua.nix.balaniuk.javacodeset.tool.JavaCodeExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

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
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JavaCodeExecutor javaCodeExecutor;

    @Value("${jwt.token.prefix}")
    private String prefix;

    private UserEntity userEntity = EntityExampleStorage.getUserEntity();
    private final HttpHeaders headers = new HttpHeaders();

    @Before
    public void setup() {
        userEntity.setStatus(UserStatus.ACTIVE);
        userRepository.save(userEntity);
        grantDeveloperAuthority();

        String token = prefix + jwtTokenProvider
                .createToken(userEntity.getUsername(), userEntity.getAuthorities());
        headers.add("Authorization", token);
    }

    @After
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    public void isExecutorEnabled_shodReturnBoolean() {
        String endpoint = buildEndpointPath("/api/executor/is-enabled");

        given(javaCodeExecutor.isEnabled()).willReturn(true);

        ResponseEntity<Boolean> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());

        verify(javaCodeExecutor).isEnabled();
    }

    @Test
    public void enableExecutor_shodEnableExecutor() {
        String endpoint = buildEndpointPath("/api/executor/enable");

        willDoNothing().given(javaCodeExecutor).enable();

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(javaCodeExecutor).enable();
    }

    @Test
    public void disableExecutor_shodDisableExecutor() {
        String endpoint = buildEndpointPath("/api/executor/disable");

        willDoNothing().given(javaCodeExecutor).disable();

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(javaCodeExecutor).disable();
    }

    @Test
    public void execute_validData_shodReturnJavaCodeExecutionResponseDto() {
        String endpoint = buildEndpointPath("/api/executor/execute");
        JavaCodeExecutionRequestDto body = DtoExampleStorage.getJavaCodeExecutionRequestDto();

        given(javaCodeExecutor.execute(any(String.class), any(String[].class), any(Boolean.class)))
                .willReturn(new JavaCodeExecutionResponseDto());

        ResponseEntity<JavaCodeExecutionResponseDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), JavaCodeExecutionResponseDto.class);

        assertEquals(new JavaCodeExecutionResponseDto(), response.getBody());

        verify(javaCodeExecutor).execute(any(String.class), any(String[].class), any(Boolean.class));
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
