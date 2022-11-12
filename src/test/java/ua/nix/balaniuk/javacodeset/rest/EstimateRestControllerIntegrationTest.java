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
import ua.nix.balaniuk.javacodeset.dto.EstimateDto;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.EstimateEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.EstimateType;
import ua.nix.balaniuk.javacodeset.enumeration.UserStatus;
import ua.nix.balaniuk.javacodeset.example.DtoExampleStorage;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;
import ua.nix.balaniuk.javacodeset.repository.CodeBlockRepository;
import ua.nix.balaniuk.javacodeset.repository.EstimateRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.security.jwt.JwtTokenProvider;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EstimateRestControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EstimateRepository estimateRepository;

    @Autowired
    private CodeBlockRepository codeBlockRepository;

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
        userEntity = userRepository.save(userEntity);
        String token = prefix + jwtTokenProvider
                .createToken(userEntity.getUsername(), userEntity.getAuthorities());
        headers.add("Authorization", token);

        patchRestTemplate = restTemplate.getRestTemplate();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @After
    public void clear() {
        estimateRepository.deleteAll();
        codeBlockRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createEstimate_validData_shodReturnCreatedEstimateDto() {
        String endpoint = buildEndpointPath("/api/estimates/create");
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        EstimateDto body = DtoExampleStorage.getEstimateDto(userEntity.getId(), codeBlockEntity.getId());

        ResponseEntity<EstimateDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), EstimateDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    @Test
    public void getEstimateById_validId_shodReturnEstimateDto() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        EstimateEntity estimateEntity =
                estimateRepository.save(EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity));
        String endpoint = buildEndpointPath("/api/estimates/get/" + estimateEntity.getId());

        ResponseEntity<EstimateDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), EstimateDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    @Test
    public void updateEstimate_validData_shodReturnUpdatedEstimateDto() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        EstimateEntity estimateEntity =
                estimateRepository.save(EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity));
        EstimateDto body = DtoExampleStorage.getEstimateDto(userEntity.getId(), codeBlockEntity.getId());
        body.setId(estimateEntity.getId());
        body.setType(EstimateType.DISLIKE);
        String endpoint = buildEndpointPath("/api/estimates/update");

        ResponseEntity<EstimateDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(body, headers), EstimateDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(body.getType(), Objects.requireNonNull(response.getBody()).getType());
    }

    @Test
    public void deleteEstimateById_validId_shodDeleteEstimateEntity() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        EstimateEntity estimateEntity =
                estimateRepository.save(EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity));
        String endpoint = buildEndpointPath("/api/estimates/delete/" + estimateEntity.getId());

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(estimateRepository.findById(estimateEntity.getId()).isPresent());
    }

    @Test
    public void getAllEstimatesByCodeBlockId_estimateEntityExist_shodReturnNotEmptyEstimateDtoList() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        estimateRepository.save(EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity));
        String endpoint = buildEndpointPath("/api/estimates/get-all/by-block-id/" + codeBlockEntity.getId());

        ResponseEntity<EstimateDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), EstimateDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    private String buildEndpointPath(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }
}
