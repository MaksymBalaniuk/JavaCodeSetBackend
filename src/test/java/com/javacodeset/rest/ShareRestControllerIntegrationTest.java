package com.javacodeset.rest;

import com.javacodeset.dto.EstimateDto;
import com.javacodeset.dto.ShareDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.ShareEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.UserStatus;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.ShareRepository;
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
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShareRestControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShareRepository shareRepository;

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
        shareRepository.deleteAll();
        codeBlockRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createShare_validData_shouldReturnCreatedShareDto() {
        UserEntity toUserEntity =
                userRepository.save(EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com"));
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        ShareDto body = DtoExampleStorage.getShareDto(
                toUserEntity.getId(), userEntity.getId(), codeBlockEntity.getId());

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(port, "/api/shares/create");

        ResponseEntity<EstimateDto> response = restTemplate.exchange(
                endpoint, HttpMethod.POST, new HttpEntity<>(body, headers), EstimateDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void getShareById_validId_shouldReturnShareDto() {
        UserEntity toUserEntity =
                userRepository.save(EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com"));
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        ShareEntity shareEntity =
                shareRepository.save(EntityExampleStorage.getShareEntity(toUserEntity, userEntity, codeBlockEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/shares/get/" + shareEntity.getId());

        ResponseEntity<EstimateDto> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), EstimateDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shareEntity.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void deleteShareById_validId_shouldDeleteShareEntity() {
        UserEntity toUserEntity =
                userRepository.save(EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com"));
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        ShareEntity shareEntity =
                shareRepository.save(EntityExampleStorage.getShareEntity(toUserEntity, userEntity, codeBlockEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/shares/delete/" + shareEntity.getId());

        ResponseEntity<Object> response = restTemplate.exchange(
                endpoint, HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(shareRepository.findById(shareEntity.getId()).isPresent());
    }

    @Test
    public void getAllSharesToUserId_shareEntityExist_shouldReturnNotEmptyShareDtoList() {
        UserEntity toUserEntity =
                userRepository.save(EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com"));
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        shareRepository.save(EntityExampleStorage.getShareEntity(toUserEntity, userEntity, codeBlockEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/shares/get-all/to-user/" + toUserEntity.getId());

        ResponseEntity<EstimateDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), EstimateDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void getAllSharesFromUserId_shareEntityExist_shouldReturnNotEmptyShareDtoList() {
        UserEntity toUserEntity =
                userRepository.save(EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com"));
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        shareRepository.save(EntityExampleStorage.getShareEntity(toUserEntity, userEntity, codeBlockEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/shares/get-all/from-user/" + userEntity.getId());

        ResponseEntity<EstimateDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), EstimateDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    public void getAllSharesOfCodeBlockId_shareEntityExist_shouldReturnNotEmptyShareDtoList() {
        UserEntity toUserEntity =
                userRepository.save(EntityExampleStorage.getUserEntity("2", "2", "2@gmail.com"));
        CodeBlockEntity codeBlockEntity =
                codeBlockRepository.save(EntityExampleStorage.getCodeBlockEntity(userEntity));
        shareRepository.save(EntityExampleStorage.getShareEntity(toUserEntity, userEntity, codeBlockEntity));

        String endpoint = RestControllerIntegrationTestUtils.buildEndpointPath(
                port, "/api/shares/get-all/by-block-id/" + codeBlockEntity.getId());

        ResponseEntity<EstimateDto[]> response = restTemplate.exchange(
                endpoint, HttpMethod.GET, new HttpEntity<>(headers), EstimateDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).hasSize(1);
    }
}
