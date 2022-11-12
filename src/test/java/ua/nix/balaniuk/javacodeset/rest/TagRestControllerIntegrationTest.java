package ua.nix.balaniuk.javacodeset.rest;

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
import ua.nix.balaniuk.javacodeset.dto.TagDto;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.TagEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.UserStatus;
import ua.nix.balaniuk.javacodeset.example.DtoExampleStorage;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;
import ua.nix.balaniuk.javacodeset.repository.CodeBlockRepository;
import ua.nix.balaniuk.javacodeset.repository.TagRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.security.jwt.JwtTokenProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    private JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.token.prefix}")
    private String prefix;

    private UserEntity userEntity = EntityExampleStorage.getUserEntity();
    private final HttpHeaders headers = new HttpHeaders();

    @Before
    public void setup() {
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity = userRepository.save(userEntity);
        String token = prefix + jwtTokenProvider
                .createToken(userEntity.getUsername(), userEntity.getAuthorities());
        headers.add("Authorization", token);
    }

    @After
    public void clear() {
        tagRepository.deleteAll();
        codeBlockRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createTag_validData_shodReturnCreatedTagDto() {
        String endpoint = buildEndpointPath("/api/tags/create");
        TagDto body = DtoExampleStorage.getTagDto();

        ResponseEntity<TagDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), TagDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    @Test
    public void getTagById_validId_shodReturnTagDto() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();
        tagEntity = tagRepository.save(tagEntity);
        String endpoint = buildEndpointPath("/api/tags/get/" + tagEntity.getId());

        ResponseEntity<TagDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), TagDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasNoNullFieldsOrProperties();
    }

    @Test
    public void getAllTagsByCodeBlockId_tagEntityExist_shodReturnTagDtoList() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        tagEntity.getCodeBlocks().add(codeBlockEntity);
        tagRepository.save(tagEntity);
        codeBlockEntity.getTags().add(tagEntity);
        codeBlockEntity = codeBlockRepository.save(codeBlockEntity);
        String endpoint = buildEndpointPath("/api/tags/get-all/by-block-id/" + codeBlockEntity.getId());

        ResponseEntity<TagDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), TagDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void addTagToCodeBlock_validData_shodAddTagEntityToCodeBlockEntity() {
        TagEntity tagEntity = tagRepository.save(EntityExampleStorage.getTagEntity());
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        String endpoint = buildEndpointPath("/api/tags/add/tag-to-block/" +
                tagEntity.getId() + "/" + codeBlockEntity.getId());

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void deleteTagFromCodeBlock_validData_shodDeleteTagEntityFromCodeBlockEntity() {
        TagEntity tagEntity = tagRepository.save(EntityExampleStorage.getTagEntity());
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        tagEntity.getCodeBlocks().add(codeBlockEntity);
        codeBlockEntity.getTags().add(tagEntity);
        tagRepository.save(tagEntity);
        codeBlockRepository.save(codeBlockEntity);
        String endpoint = buildEndpointPath("/api/tags/delete/tag-from-block/" +
                tagEntity.getId() + "/" + codeBlockEntity.getId());

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    private String buildEndpointPath(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }
}
