package com.javacodeset.service;

import com.javacodeset.dto.EstimateDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.EstimateEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.EstimateType;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.EstimateRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.service.impl.EstimateServiceImplementation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EstimateServiceImplementationTest {

    @Mock
    private EstimateRepository estimateRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CodeBlockRepository codeBlockRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EstimateServiceImplementation estimateService;

    private static final UUID TEST_ESTIMATE_UUID = UUID.randomUUID();
    private static final UUID TEST_USER_UUID = UUID.randomUUID();
    private static final UUID TEST_CODE_BLOCK_UUID = UUID.randomUUID();

    @Test
    public void create_validData_shouldReturnSavedEstimateEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        EstimateEntity estimateEntity = EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity);
        EstimateDto estimateDto = DtoExampleStorage.getEstimateDto(TEST_USER_UUID, TEST_CODE_BLOCK_UUID);

        given(userRepository.findById(estimateDto.getUserId())).willReturn(Optional.of(userEntity));
        given(codeBlockRepository.findById(estimateDto.getCodeBlockId())).willReturn(Optional.of(codeBlockEntity));
        given(modelMapper.map(estimateDto, EstimateEntity.class)).willReturn(estimateEntity);
        given(estimateRepository.save(estimateEntity)).willReturn(estimateEntity);
        given(userRepository.save(userEntity)).willReturn(userEntity);
        given(codeBlockRepository.save(codeBlockEntity)).willReturn(codeBlockEntity);

        EstimateEntity actual = estimateService.create(estimateDto);

        assertNotNull(actual);

        verify(estimateRepository).save(any(EstimateEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void create_invalidUserId_shouldThrowException() {
        EstimateDto estimateDto = DtoExampleStorage.getEstimateDto(TEST_USER_UUID, TEST_CODE_BLOCK_UUID);
        given(userRepository.findById(estimateDto.getUserId())).willReturn(Optional.empty());
        estimateService.create(estimateDto);
    }

    @Test(expected = NotFoundException.class)
    public void create_invalidCodeBlockId_shouldThrowException() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        EstimateDto estimateDto = DtoExampleStorage.getEstimateDto(TEST_USER_UUID, TEST_CODE_BLOCK_UUID);
        given(userRepository.findById(estimateDto.getUserId())).willReturn(Optional.of(userEntity));
        given(codeBlockRepository.findById(estimateDto.getCodeBlockId())).willReturn(Optional.empty());
        estimateService.create(estimateDto);
    }

    @Test
    public void get_validId_shouldReturnEstimateEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        EstimateEntity estimateEntity = EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity);

        given(estimateRepository.findById(TEST_ESTIMATE_UUID)).willReturn(Optional.of(estimateEntity));

        EstimateEntity actual = estimateService.get(TEST_ESTIMATE_UUID);

        assertNotNull(actual);
    }

    @Test(expected = NotFoundException.class)
    public void get_invalidId_shouldThrowException() {
        given(estimateRepository.findById(TEST_ESTIMATE_UUID)).willReturn(Optional.empty());
        estimateService.get(TEST_ESTIMATE_UUID);
    }

    @Test
    public void getAll_shouldReturnEstimateEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        EstimateEntity estimateEntity = EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity);
        List<EstimateEntity> expected = List.of(estimateEntity);

        given(estimateRepository.findAll()).willReturn(expected);

        List<EstimateEntity> actual = estimateService.getAll();

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void update_validData_shouldReturnUpdatedEstimateEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        EstimateEntity estimateEntity = EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity);
        estimateEntity.setType(EstimateType.DISLIKE);
        EstimateDto estimateDto = DtoExampleStorage.getEstimateDto(TEST_USER_UUID, TEST_CODE_BLOCK_UUID);
        estimateDto.setId(TEST_ESTIMATE_UUID);
        estimateDto.setType(EstimateType.LIKE);

        given(estimateRepository.findById(TEST_ESTIMATE_UUID)).willReturn(Optional.of(estimateEntity));
        given(estimateRepository.save(estimateEntity)).willReturn(estimateEntity);

        EstimateEntity actual = estimateService.update(estimateDto);

        assertEquals(EstimateType.LIKE, actual.getType());

        verify(estimateRepository).save(any(EstimateEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void update_invalidEstimateId_shouldThrowException() {
        EstimateDto estimateDto = DtoExampleStorage.getEstimateDto(TEST_USER_UUID, TEST_CODE_BLOCK_UUID);
        estimateDto.setId(TEST_ESTIMATE_UUID);
        given(estimateRepository.findById(estimateDto.getId())).willReturn(Optional.empty());
        estimateService.update(estimateDto);
    }

    @Test
    public void delete_estimateEntityExist_shouldDeleteEstimateEntity() {
        given(estimateRepository.existsById(TEST_ESTIMATE_UUID)).willReturn(true);
        willDoNothing().given(estimateRepository).deleteById(TEST_ESTIMATE_UUID);
        estimateService.delete(TEST_ESTIMATE_UUID);
        verify(estimateRepository).deleteById(any(UUID.class));
    }

    @Test
    public void getAllEstimatesByCodeBlockId_estimateEntityExist_shouldReturnNotEmptyEstimateEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        EstimateEntity estimateEntity = EntityExampleStorage.getEstimateEntity(userEntity, codeBlockEntity);
        List<EstimateEntity> expected = List.of(estimateEntity);

        given(estimateRepository.findAllByCodeBlockId(TEST_CODE_BLOCK_UUID)).willReturn(expected);

        List<EstimateEntity> actual = estimateService.getAllEstimatesByCodeBlockId(TEST_CODE_BLOCK_UUID);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }
}
