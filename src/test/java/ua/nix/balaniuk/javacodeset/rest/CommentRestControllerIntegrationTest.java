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
import ua.nix.balaniuk.javacodeset.dto.CommentDto;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.CommentEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.UserStatus;
import ua.nix.balaniuk.javacodeset.example.DtoExampleStorage;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;
import ua.nix.balaniuk.javacodeset.repository.CodeBlockRepository;
import ua.nix.balaniuk.javacodeset.repository.CommentRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.security.jwt.JwtTokenProvider;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentRestControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

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
        commentRepository.deleteAll();
        codeBlockRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createComment_validData_shodReturnCreatedCommentDto() {
        String endpoint = buildEndpointPath("/api/comments/create");
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        CommentDto body = DtoExampleStorage.getCommentDto(userEntity.getId(), codeBlockEntity.getId());

        ResponseEntity<CommentDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), CommentDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void getCommentById_validId_shodReturnCommentDto() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        CommentEntity commentEntity =
                commentRepository.save(EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity));
        String endpoint = buildEndpointPath("/api/comments/get/" + commentEntity.getId());

        ResponseEntity<CommentDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), CommentDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void updateComment_validData_shodReturnUpdatedCommentDto() {
        String endpoint = buildEndpointPath("/api/comments/update");
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        CommentEntity commentEntity =
                commentRepository.save(EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity));
        CommentDto body = DtoExampleStorage.getCommentDto(userEntity.getId(), codeBlockEntity.getId());
        body.setId(commentEntity.getId());
        body.setComment("test comment");

        ResponseEntity<CommentDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(body, headers), CommentDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(body.getComment(), Objects.requireNonNull(response.getBody()).getComment());
    }

    @Test
    public void deleteCommentById_validId_shodDeleteEstimateEntity() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        CommentEntity commentEntity =
                commentRepository.save(EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity));
        String endpoint = buildEndpointPath("/api/comments/delete/" + commentEntity.getId());

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(commentRepository.findById(commentEntity.getId()).isPresent());
    }

    @Test
    public void getAllCommentsByCodeBlockId_commentEntityExist_shodReturnNotEmptyCommentDtoList() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        commentRepository.save(EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity));
        String endpoint = buildEndpointPath("/api/comments/get-all/by-block-id/" + codeBlockEntity.getId());

        ResponseEntity<CommentDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), CommentDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    private String buildEndpointPath(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }
}
