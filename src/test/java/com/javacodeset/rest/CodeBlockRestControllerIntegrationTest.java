package com.javacodeset.rest;

import com.javacodeset.dto.CodeBlockDto;
import com.javacodeset.dto.filter.FilterCodeBlockDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.EstimateEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.CodeBlockType;
import com.javacodeset.enumeration.EstimateType;
import com.javacodeset.enumeration.UserPremium;
import com.javacodeset.enumeration.UserStatus;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.EstimateRepository;
import com.javacodeset.repository.ShareRepository;
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
public class CodeBlockRestControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeBlockRepository codeBlockRepository;

    @Autowired
    private EstimateRepository estimateRepository;

    @Autowired
    private ShareRepository shareRepository;

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
        userEntity.setPremium(UserPremium.NONE);
        userEntity = userRepository.save(userEntity);
        RestControllerIntegrationTestUtils.resetAuthorizationHeader(headers, jwtProvider, jwtPrefix, userEntity);

        patchRestTemplate = restTemplate.getRestTemplate();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @After
    public void clear() {
        shareRepository.deleteAll();
        estimateRepository.deleteAll();
        codeBlockRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createCodeBlock_validData_shouldReturnCreatedCodeBlockDto() {
        CodeBlockDto body = DtoExampleStorage.getCodeBlockDto(userEntity.getId());
        body.setContent("");

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/blocks/create");

        ResponseEntity<CodeBlockDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), CodeBlockDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void getCodeBlockById_validId_shouldReturnCodeBlockDto() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/blocks/get/" + codeBlockEntity.getId());

        ResponseEntity<CodeBlockDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), CodeBlockDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(codeBlockEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void updateCodeBlock_validData_shouldReturnUpdatedCodeBlockDto() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        CodeBlockDto body = DtoExampleStorage.getCodeBlockDto(userEntity.getId());
        body.setId(codeBlockEntity.getId());
        body.setDescription("description");
        body.setContent("");

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/blocks/update");

        ResponseEntity<CodeBlockDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(body, headers), CodeBlockDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(body.getDescription(), Objects.requireNonNull(response.getBody()).getDescription());
    }

    @Test
    public void deleteCodeBlockById_validId_shouldDeleteCodeBlockEntity() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/blocks/delete/" + codeBlockEntity.getId());

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(codeBlockRepository.findById(codeBlockEntity.getId()).isPresent());
    }

    @Test
    public void getAllCodeBlocksByUserId_codeBlockEntityExist_shouldReturnNotEmptyCodeBlockDtoList() {
        codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/blocks/get-all/by-user-id/" + userEntity.getId());

        ResponseEntity<CodeBlockDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), CodeBlockDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void getAllFilteredCodeBlocks_codeBlockEntityExist_shouldReturnNotEmptyCodeBlockDtoList() {
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setTitle("title");
        codeBlockEntity.setType(CodeBlockType.PRIVATE);
        codeBlockRepository.save(codeBlockEntity);
        FilterCodeBlockDto body = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOn();
        body.setFilterQuery(codeBlockEntity.getTitle());
        body.setAllowedTypes(new CodeBlockType[] { CodeBlockType.PRIVATE });

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/blocks/get-all/filtered");

        ResponseEntity<CodeBlockDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), CodeBlockDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void getAllFilteredCodeBlocksByUserId_codeBlockEntityExist_shouldReturnNotEmptyCodeBlockDtoList() {
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setTitle("title");
        codeBlockEntity.setType(CodeBlockType.PRIVATE);
        codeBlockRepository.save(codeBlockEntity);
        FilterCodeBlockDto body = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOn();
        body.setFilterQuery(codeBlockEntity.getTitle());
        body.setAllowedTypes(new CodeBlockType[] { CodeBlockType.PRIVATE });

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/blocks/get-all/by-user-id/" + userEntity.getId() + "/filtered");

        ResponseEntity<CodeBlockDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), CodeBlockDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void getAllFilteredCodeBlocksByUserIdAndEstimateType_codeBlockEntityExist_shouldReturnNotEmptyCodeBlockDtoList() {
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setTitle("title");
        codeBlockEntity.setType(CodeBlockType.PRIVATE);
        codeBlockRepository.save(codeBlockEntity);
        EstimateEntity estimateEntity = EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity);
        estimateEntity.setType(EstimateType.LIKE);
        estimateRepository.save(estimateEntity);
        FilterCodeBlockDto body = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOn();
        body.setFilterQuery(codeBlockEntity.getTitle());
        body.setAllowedTypes(new CodeBlockType[] { CodeBlockType.PRIVATE });

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/blocks/get-all/by-user-id-and-estimate-type/" +
                        userEntity.getId() + "/" + EstimateType.LIKE + "/filtered");

        ResponseEntity<CodeBlockDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), CodeBlockDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void getAllFilteredCodeBlocksSharedFromUserIdToUserId_codeBlockEntityExist_shouldReturnNotEmptyCodeBlockDtoList() {
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setTitle("title");
        codeBlockEntity.setType(CodeBlockType.PRIVATE);
        codeBlockRepository.save(codeBlockEntity);
        UserEntity targetUserEntity = userRepository.save(
                EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com"));
        shareRepository.save(EntityExampleStorage.getShareEntity(targetUserEntity, userEntity, codeBlockEntity));
        FilterCodeBlockDto body = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOn();
        body.setFilterQuery(codeBlockEntity.getTitle());
        body.setAllowedTypes(new CodeBlockType[] { CodeBlockType.PRIVATE });

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/blocks/get-all/shared-from-user-id-to-user-id/" +
                        userEntity.getId() + "/" + targetUserEntity.getId() + "/filtered");

        ResponseEntity<CodeBlockDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), CodeBlockDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }
}
