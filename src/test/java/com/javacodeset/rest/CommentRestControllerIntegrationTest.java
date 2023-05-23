package com.javacodeset.rest;

import com.javacodeset.dto.CommentDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.CommentEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.UserStatus;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.CommentRepository;
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
        commentRepository.deleteAll();
        codeBlockRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createComment_validData_shouldReturnCreatedCommentDto() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        CommentDto body = DtoExampleStorage.getCommentDto(userEntity.getId(), codeBlockEntity.getId());

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/comments/create");

        ResponseEntity<CommentDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), CommentDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void getCommentById_validId_shouldReturnCommentDto() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        CommentEntity commentEntity =
                commentRepository.save(EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/comments/get/" + commentEntity.getId());

        ResponseEntity<CommentDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), CommentDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void updateComment_validData_shouldReturnUpdatedCommentDto() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        CommentEntity commentEntity =
                commentRepository.save(EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity));
        CommentDto body = DtoExampleStorage.getCommentDto(userEntity.getId(), codeBlockEntity.getId());
        body.setId(commentEntity.getId());
        body.setComment("comment");

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/comments/update");

        ResponseEntity<CommentDto> response = patchRestTemplate.exchange(
                endpoint, HttpMethod.PATCH, new HttpEntity<>(body, headers), CommentDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(body.getComment(), Objects.requireNonNull(response.getBody()).getComment());
    }

    @Test
    public void deleteCommentById_validId_shouldDeleteCommentEntity() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        CommentEntity commentEntity =
                commentRepository.save(EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/comments/delete/" + commentEntity.getId());

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(commentRepository.findById(commentEntity.getId()).isPresent());
    }

    @Test
    public void getAllCommentsByCodeBlockId_commentEntityExist_shouldReturnNotEmptyCommentDtoList() {
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        commentRepository.save(EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/comments/get-all/by-block-id/" + codeBlockEntity.getId());

        ResponseEntity<CommentDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), CommentDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }
}
