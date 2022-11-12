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
import ua.nix.balaniuk.javacodeset.dto.CodeBlockDto;
import ua.nix.balaniuk.javacodeset.dto.filter.FilterCodeBlockDto;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.EstimateEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.CodeBlockType;
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
    public void createCodeBlock_validData_shodReturnCreatedCommentDto() {
        String endpoint = buildEndpointPath("/api/blocks/create");
        CodeBlockDto body = DtoExampleStorage.getCodeBlockDto(userEntity.getId());

        ResponseEntity<CodeBlockDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), CodeBlockDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void getCodeBlockById_validId_shodReturnCommentDto() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        String endpoint = buildEndpointPath("/api/blocks/get/" + codeBlockEntity.getId());

        ResponseEntity<CodeBlockDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), CodeBlockDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(codeBlockEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void updateCodeBlock_validData_shodReturnUpdatedCommentDto() {
        String endpoint = buildEndpointPath("/api/blocks/update");
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        CodeBlockDto body = DtoExampleStorage.getCodeBlockDto(userEntity.getId());
        body.setId(codeBlockEntity.getId());
        body.setDescription("test description");

        ResponseEntity<CodeBlockDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(body, headers), CodeBlockDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(body.getDescription(), Objects.requireNonNull(response.getBody()).getDescription());
    }

    @Test
    public void deleteCodeBlockById_validId_shodDeleteEstimateEntity() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        String endpoint = buildEndpointPath("/api/blocks/delete/" + codeBlockEntity.getId());

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(codeBlockRepository.findById(codeBlockEntity.getId()).isPresent());
    }

    @Test
    public void getAllCodeBlocksByUserId_codeBlockEntityExist_shodReturnNotEmptyCodeBlockDtoList() {
        codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        String endpoint = buildEndpointPath("/api/blocks/get-all/by-user-id/" + userEntity.getId());

        ResponseEntity<CodeBlockDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), CodeBlockDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void getAllCodeBlocksByType_codeBlockEntityExist_shodReturnNotEmptyCodeBlockDtoList() {
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setType(CodeBlockType.PUBLIC);
        codeBlockRepository.save(codeBlockEntity);
        String endpoint = buildEndpointPath("/api/blocks/get-all/by-block-type/" + codeBlockEntity.getType());

        ResponseEntity<CodeBlockDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), CodeBlockDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void getAllFilteredCodeBlocks_shodReturnNotEmptyCodeBlockDtoList() {
        String endpoint = buildEndpointPath("/api/blocks/get-all/filtered");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setTitle("test title");
        codeBlockRepository.save(codeBlockEntity);
        FilterCodeBlockDto body = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOn();
        body.setFilterQuery(codeBlockEntity.getTitle());

        ResponseEntity<CodeBlockDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), CodeBlockDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void getAllFilteredCodeBlocksByUserId_shodReturnNotEmptyCodeBlockDtoList() {
        String endpoint = buildEndpointPath("/api/blocks/get-all/by-user-id/" + userEntity.getId() + "/filtered");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setTitle("test title");
        codeBlockRepository.save(codeBlockEntity);
        FilterCodeBlockDto body = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOn();
        body.setFilterQuery(codeBlockEntity.getTitle());

        ResponseEntity<CodeBlockDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), CodeBlockDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void getAllFilteredCodeBlocksByUserIdAndEstimateType_shodReturnNotEmptyCodeBlockDtoList() {
        String endpoint = buildEndpointPath("/api/blocks/get-all/by-user-id-and-estimate-type/" +
                userEntity.getId() + "/" + EstimateType.LIKE + "/filtered");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setTitle("test title");
        codeBlockRepository.save(codeBlockEntity);
        EstimateEntity estimateEntity = EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity);
        estimateEntity.setType(EstimateType.LIKE);
        estimateRepository.save(estimateEntity);
        FilterCodeBlockDto body = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOn();
        body.setFilterQuery(codeBlockEntity.getTitle());

        ResponseEntity<CodeBlockDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), CodeBlockDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    private String buildEndpointPath(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }
}
