package com.javacodeset.rest;

import com.javacodeset.dto.TagDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.TagEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.UserStatus;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.TagRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.security.jwt.JwtProvider;
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

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TagRestControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CodeBlockRepository codeBlockRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Value("${jwt.token.prefix}")
    private String jwtPrefix;

    private UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
    private final HttpHeaders headers = new HttpHeaders();

    @Before
    public void setup() {
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity = userRepository.save(userEntity);
        RestControllerIntegrationTestUtils.resetAuthorizationHeader(headers, jwtProvider, jwtPrefix, userEntity);
    }

    @After
    public void clear() {
        tagRepository.deleteAll();
        codeBlockRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createTag_validData_shouldReturnCreatedTagDto() {
        TagDto body = DtoExampleStorage.getTagDto("#stream");

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/tags/create");

        ResponseEntity<TagDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), TagDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void getTagById_validId_shouldReturnTagDto() {
        TagEntity tagEntity = tagRepository.save(EntityExampleStorage.getTagEntity("#stream"));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/tags/get/" + tagEntity.getId());

        ResponseEntity<TagDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), TagDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void getAllTagsByCodeBlockId_tagEntityExist_shouldReturnTagDtoList() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity("#stream");
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        tagEntity.getCodeBlocks().add(codeBlockEntity);
        tagRepository.save(tagEntity);
        codeBlockEntity.getTags().add(tagEntity);
        codeBlockRepository.save(codeBlockEntity);

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/tags/get-all/by-block-id/" + codeBlockEntity.getId());

        ResponseEntity<TagDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), TagDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void addTagToCodeBlock_validData_shouldAddTagEntityToCodeBlockEntity() {
        TagEntity tagEntity = tagRepository.save(EntityExampleStorage.getTagEntity("#stream"));
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/tags/add/tag-to-block/" + tagEntity.getId() + "/" + codeBlockEntity.getId());

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void deleteTagFromCodeBlock_validData_shouldDeleteTagEntityFromCodeBlockEntity() {
        TagEntity tagEntity = tagRepository.save(EntityExampleStorage.getTagEntity("#stream"));
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        tagEntity.getCodeBlocks().add(codeBlockEntity);
        tagRepository.save(tagEntity);
        codeBlockEntity.getTags().add(tagEntity);
        codeBlockRepository.save(codeBlockEntity);

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/tags/delete/tag-from-block/" + tagEntity.getId() + "/" + codeBlockEntity.getId());

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
