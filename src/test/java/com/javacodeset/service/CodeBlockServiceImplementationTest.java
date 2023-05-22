package com.javacodeset.service;

import com.javacodeset.dto.CodeBlockDto;
import com.javacodeset.dto.filter.FilterCodeBlockDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.CodeBlockType;
import com.javacodeset.enumeration.EstimateType;
import com.javacodeset.enumeration.UserPremium;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.service.impl.CodeBlockServiceImplementation;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CodeBlockServiceImplementationTest {

    @Mock
    private CodeBlockRepository codeBlockRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CodeBlockServiceImplementation codeBlockService;

    private static final UUID TEST_CODE_BLOCK_UUID = UUID.randomUUID();
    private static final UUID TEST_USER_UUID = UUID.randomUUID();

    @Test
    public void create_validData_shouldReturnSavedCodeBlockEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setPremium(UserPremium.NONE);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        CodeBlockDto codeBlockDto = DtoExampleStorage.getCodeBlockDto(TEST_USER_UUID);
        codeBlockDto.setContent("");

        given(userRepository.findById(codeBlockDto.getUserId())).willReturn(Optional.of(userEntity));
        given(modelMapper.map(codeBlockDto, CodeBlockEntity.class)).willReturn(codeBlockEntity);
        given(codeBlockRepository.save(codeBlockEntity)).willReturn(codeBlockEntity);
        given(userRepository.save(userEntity)).willReturn(userEntity);

        CodeBlockEntity actual = codeBlockService.create(codeBlockDto);

        assertNotNull(actual);

        verify(codeBlockRepository).save(any(CodeBlockEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void create_invalidUserId_shouldThrowException() {
        CodeBlockDto codeBlockDto = DtoExampleStorage.getCodeBlockDto(TEST_USER_UUID);
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        codeBlockService.create(codeBlockDto);
    }

    @Test
    public void get_validId_shouldReturnCodeBlockEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.of(codeBlockEntity));
        CodeBlockEntity actual = codeBlockService.get(TEST_CODE_BLOCK_UUID);
        assertNotNull(actual);
    }

    @Test(expected = NotFoundException.class)
    public void get_invalidId_shouldThrowException() {
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.empty());
        codeBlockService.get(TEST_CODE_BLOCK_UUID);
    }

    @Test
    public void getAll_shouldReturnCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        given(codeBlockRepository.findAll()).willReturn(expected);

        List<CodeBlockEntity> actual = codeBlockService.getAll();

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void update_validData_shouldReturnUpdatedCodeBlockEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        userEntity.setPremium(UserPremium.NONE);
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setDescription("");
        CodeBlockDto codeBlockDto = DtoExampleStorage.getCodeBlockDto(TEST_USER_UUID);
        codeBlockDto.setId(TEST_CODE_BLOCK_UUID);
        codeBlockDto.setDescription("Description");
        codeBlockDto.setContent("");

        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.of(codeBlockEntity));
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));

        codeBlockEntity.setDescription(codeBlockDto.getDescription());

        given(codeBlockRepository.save(codeBlockEntity)).willReturn(codeBlockEntity);
        given(userRepository.save(userEntity)).willReturn(userEntity);

        CodeBlockEntity actual = codeBlockService.update(codeBlockDto);

        assertEquals(codeBlockDto.getDescription(), actual.getDescription());

        verify(codeBlockRepository).save(any(CodeBlockEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void update_invalidCodeBlockId_shouldThrowException() {
        CodeBlockDto codeBlockDto = DtoExampleStorage.getCodeBlockDto(TEST_USER_UUID);
        codeBlockDto.setId(TEST_CODE_BLOCK_UUID);
        given(codeBlockRepository.findById(codeBlockDto.getId())).willReturn(Optional.empty());
        codeBlockService.update(codeBlockDto);
    }

    @Test(expected = NotFoundException.class)
    public void update_invalidUserId_shouldThrowException() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        CodeBlockDto codeBlockDto = DtoExampleStorage.getCodeBlockDto(TEST_USER_UUID);
        codeBlockDto.setId(TEST_CODE_BLOCK_UUID);

        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.of(codeBlockEntity));
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());

        codeBlockService.update(codeBlockDto);
    }

    @Test
    public void delete_codeBlockEntityExist_shouldDeleteCodeBlockEntity() {
        given(codeBlockRepository.existsById(TEST_CODE_BLOCK_UUID)).willReturn(true);
        willDoNothing().given(codeBlockRepository).deleteById(TEST_CODE_BLOCK_UUID);
        codeBlockService.delete(TEST_CODE_BLOCK_UUID);
        verify(codeBlockRepository).deleteById(any(UUID.class));
    }

    @Test
    public void getAllFilteredCodeBlocks_codeBlockEntityExist_allFiltersOff_shouldReturnEmptyList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        String filterQuery = "query";
        codeBlockEntity.setTitle(filterQuery);
        codeBlockEntity.setDescription(filterQuery);
        codeBlockEntity.setContent(filterQuery);
        codeBlockEntity.setType(CodeBlockType.PRIVATE);
        FilterCodeBlockDto filterCodeBlockDto = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOff();
        filterCodeBlockDto.setFilterQuery(filterQuery);
        filterCodeBlockDto.setAllowedTypes(new CodeBlockType[] { CodeBlockType.PRIVATE });
        List<CodeBlockEntity> codeBlockEntityList = List.of(codeBlockEntity);

        given(codeBlockRepository.findAll()).willReturn(codeBlockEntityList);

        List<CodeBlockEntity> actual = codeBlockService.getAllFilteredCodeBlocks(filterCodeBlockDto);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void getAllFilteredCodeBlocks_codeBlockEntityExist_emptyFilterQuery_shouldReturnNotEmptyList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setType(CodeBlockType.PRIVATE);
        FilterCodeBlockDto filterCodeBlockDto = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOff();
        filterCodeBlockDto.setFilterQuery("");
        filterCodeBlockDto.setAllowedTypes(new CodeBlockType[] { CodeBlockType.PRIVATE });
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        given(codeBlockRepository.findAll()).willReturn(expected);

        List<CodeBlockEntity> actual = codeBlockService.getAllFilteredCodeBlocks(filterCodeBlockDto);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void getAllFilteredCodeBlocks_emptySource_shouldReturnEmptyCodeBlockEntityList() {
        FilterCodeBlockDto filterCodeBlockDto = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOff();
        given(codeBlockRepository.findAll()).willReturn(List.of());
        List<CodeBlockEntity> actual = codeBlockService.getAllFilteredCodeBlocks(filterCodeBlockDto);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void getAllFilteredCodeBlocks_emptyAllowedTypes_shouldReturnEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setType(CodeBlockType.PRIVATE);
        FilterCodeBlockDto filterCodeBlockDto = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOff();
        filterCodeBlockDto.setAllowedTypes(new CodeBlockType[] {});

        given(codeBlockRepository.findAll()).willReturn(List.of(codeBlockEntity));

        List<CodeBlockEntity> actual = codeBlockService.getAllFilteredCodeBlocks(filterCodeBlockDto);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void getAllCodeBlocksByUserId_codeBlockEntityExist_shouldReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        given(codeBlockRepository.findAllByUserId(TEST_USER_UUID)).willReturn(expected);

        List<CodeBlockEntity> actual = codeBlockService.getAllCodeBlocksByUserId(TEST_USER_UUID);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void getAllFilteredCodeBlocksByUserId_codeBlockEntityExist_shouldReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setType(CodeBlockType.PRIVATE);
        FilterCodeBlockDto filterCodeBlockDto = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOff();
        filterCodeBlockDto.setFilterQuery("");
        filterCodeBlockDto.setAllowedTypes(new CodeBlockType[] { CodeBlockType.PRIVATE });
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        given(codeBlockRepository.findAllByUserId(TEST_USER_UUID)).willReturn(expected);

        List<CodeBlockEntity> actual =
                codeBlockService.getAllFilteredCodeBlocksByUserId(TEST_USER_UUID, filterCodeBlockDto);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void getAllFilteredCodeBlocksByUserIdAndEstimateType_codeBlockEntityExist_shouldReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setType(CodeBlockType.PRIVATE);
        FilterCodeBlockDto filterCodeBlockDto = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOff();
        filterCodeBlockDto.setFilterQuery("");
        filterCodeBlockDto.setAllowedTypes(new CodeBlockType[] { CodeBlockType.PRIVATE });
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        given(codeBlockRepository.findAllCodeBlocksByUserIdAndEstimateType(
                TEST_USER_UUID, EstimateType.LIKE)).willReturn(expected);

        List<CodeBlockEntity> actual =
                codeBlockService.getAllFilteredCodeBlocksByUserIdAndEstimateType(
                        TEST_USER_UUID, EstimateType.LIKE, filterCodeBlockDto);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void getAllFilteredCodeBlocksSharedFromUserIdToUserId_codeBlockEntityExist_shouldReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setType(CodeBlockType.PRIVATE);
        FilterCodeBlockDto filterCodeBlockDto = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOff();
        filterCodeBlockDto.setFilterQuery("");
        filterCodeBlockDto.setAllowedTypes(new CodeBlockType[] { CodeBlockType.PRIVATE });
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        given(codeBlockRepository.findAllCodeBlocksSharedFromUserIdToUserId(
                TEST_USER_UUID, TEST_USER_UUID)).willReturn(expected);

        List<CodeBlockEntity> actual =
                codeBlockService.getAllFilteredCodeBlocksSharedFromUserIdToUserId(
                        TEST_USER_UUID, TEST_USER_UUID, filterCodeBlockDto);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }
}
