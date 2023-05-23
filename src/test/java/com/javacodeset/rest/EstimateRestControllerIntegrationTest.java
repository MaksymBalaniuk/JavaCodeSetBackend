package com.javacodeset.rest;

import com.javacodeset.dto.EstimateDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.EstimateEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.EstimateType;
import com.javacodeset.enumeration.UserStatus;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.EstimateRepository;
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
    private JwtProvider jwtProvider;

    @Value("${jwt.token.prefix}")
    private String jwtPrefix;

    private UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
    private final HttpHeaders headers = new HttpHeaders();
    private RestTemplate patchRestTemplate;

    @Before
    public void setup() {
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity = userRepository.save(userEntity);
        RestControllerIntegrationTestUtils.resetAuthorizationHeader(headers, jwtProvider, jwtPrefix, userEntity);

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
    public void createEstimate_validData_shouldReturnCreatedEstimateDto() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        EstimateDto body = DtoExampleStorage.getEstimateDto(userEntity.getId(), codeBlockEntity.getId());

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/estimates/create");

        ResponseEntity<EstimateDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), EstimateDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void getEstimateById_validId_shouldReturnEstimateDto() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        EstimateEntity estimateEntity =
                estimateRepository.save(EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/estimates/get/" + estimateEntity.getId());

        ResponseEntity<EstimateDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), EstimateDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(estimateEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void updateEstimate_validData_shouldReturnUpdatedEstimateDto() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        EstimateEntity estimateEntity = EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity);
        estimateEntity.setType(EstimateType.DISLIKE);
        estimateRepository.save(estimateEntity);
        EstimateDto body = DtoExampleStorage.getEstimateDto(userEntity.getId(), codeBlockEntity.getId());
        body.setId(estimateEntity.getId());
        body.setType(EstimateType.LIKE);

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/estimates/update");

        ResponseEntity<EstimateDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(body, headers), EstimateDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(body.getType(), Objects.requireNonNull(response.getBody()).getType());
    }

    @Test
    public void deleteEstimateById_validId_shouldDeleteEstimateEntity() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        EstimateEntity estimateEntity =
                estimateRepository.save(EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/estimates/delete/" + estimateEntity.getId());

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(estimateRepository.findById(estimateEntity.getId()).isPresent());
    }

    @Test
    public void getAllEstimatesByCodeBlockId_estimateEntityExist_shouldReturnNotEmptyEstimateDtoList() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        estimateRepository.save(EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/estimates/get-all/by-block-id/" + codeBlockEntity.getId());

        ResponseEntity<EstimateDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), EstimateDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }
}
