package ua.nix.balaniuk.javacodeset.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import ua.nix.balaniuk.javacodeset.dto.CodeBlockDto;
import ua.nix.balaniuk.javacodeset.dto.filter.FilterCodeBlockDto;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.CodeBlockType;
import ua.nix.balaniuk.javacodeset.enumeration.EstimateType;
import ua.nix.balaniuk.javacodeset.example.DtoExampleStorage;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;
import ua.nix.balaniuk.javacodeset.exception.NotFoundException;
import ua.nix.balaniuk.javacodeset.repository.CodeBlockRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.service.impl.CodeBlockServiceImplementation;

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
    public void create_validData_shodReturnSavedCodeBlockEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        CodeBlockDto codeBlockDto = DtoExampleStorage.getCodeBlockDto(TEST_USER_UUID);

        given(userRepository.findById(codeBlockDto.getUserId())).willReturn(Optional.of(userEntity));
        given(modelMapper.map(codeBlockDto, CodeBlockEntity.class)).willReturn(codeBlockEntity);
        given(codeBlockRepository.save(codeBlockEntity)).willReturn(codeBlockEntity);
        given(userRepository.save(userEntity)).willReturn(userEntity);

        CodeBlockEntity actual = codeBlockService.create(codeBlockDto);

        assertNotNull(actual);

        verify(codeBlockRepository).save(any(CodeBlockEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void create_invalidUserId_shodThrowException() {
        CodeBlockDto codeBlockDto = DtoExampleStorage.getCodeBlockDto(TEST_USER_UUID);
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        codeBlockService.create(codeBlockDto);
    }

    @Test
    public void get_validId_shodReturnCodeBlockEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);

        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.of(codeBlockEntity));

        CodeBlockEntity actual = codeBlockService.get(TEST_CODE_BLOCK_UUID);

        assertNotNull(actual);
    }

    @Test(expected = NotFoundException.class)
    public void get_invalidId_shodThrowException() {
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.empty());
        codeBlockService.get(TEST_CODE_BLOCK_UUID);
    }

    @Test
    public void getAll_shodReturnCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        given(codeBlockRepository.findAll()).willReturn(expected);

        List<CodeBlockEntity> actual = codeBlockService.getAll();

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void update_validData_shodReturnUpdatedCodeBlockEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        codeBlockEntity.setDescription("");
        CodeBlockDto codeBlockDto = DtoExampleStorage.getCodeBlockDto(TEST_USER_UUID);
        codeBlockDto.setId(TEST_CODE_BLOCK_UUID);
        codeBlockDto.setDescription("Description");

        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.of(codeBlockEntity));
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));

        codeBlockEntity.setDescription(codeBlockDto.getDescription());

        given(modelMapper.map(codeBlockDto, CodeBlockEntity.class)).willReturn(codeBlockEntity);
        given(codeBlockRepository.save(codeBlockEntity)).willReturn(codeBlockEntity);
        given(userRepository.save(userEntity)).willReturn(userEntity);

        CodeBlockEntity actual = codeBlockService.update(codeBlockDto);

        assertEquals(codeBlockDto.getDescription(), actual.getDescription());

        verify(codeBlockRepository).save(any(CodeBlockEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void update_invalidCodeBlockId_shodThrowException() {
        CodeBlockDto codeBlockDto = DtoExampleStorage.getCodeBlockDto(TEST_USER_UUID);
        codeBlockDto.setId(TEST_CODE_BLOCK_UUID);
        given(codeBlockRepository.findById(codeBlockDto.getId())).willReturn(Optional.empty());
        codeBlockService.update(codeBlockDto);
    }

    @Test(expected = NotFoundException.class)
    public void update_invalidUserId_shodThrowException() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        CodeBlockDto codeBlockDto = DtoExampleStorage.getCodeBlockDto(TEST_USER_UUID);
        codeBlockDto.setId(TEST_CODE_BLOCK_UUID);
        codeBlockDto.setUserId(TEST_USER_UUID);

        given(codeBlockRepository.findById(codeBlockDto.getId())).willReturn(Optional.of(codeBlockEntity));
        given(userRepository.findById(codeBlockDto.getUserId())).willReturn(Optional.empty());

        codeBlockService.update(codeBlockDto);
    }

    @Test
    public void delete_codeBlockEntityExist_shodDeleteCodeBlockEntity() {
        given(codeBlockRepository.existsById(TEST_CODE_BLOCK_UUID)).willReturn(true);
        willDoNothing().given(codeBlockRepository).deleteById(TEST_CODE_BLOCK_UUID);
        codeBlockService.delete(TEST_CODE_BLOCK_UUID);
        verify(codeBlockRepository).deleteById(any(UUID.class));
    }

    @Test
    public void getAllCodeBlocksByUserId_codeBlockEntityExist_shodReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        given(codeBlockRepository.findAllByUserId(TEST_USER_UUID)).willReturn(expected);

        List<CodeBlockEntity> actual = codeBlockService.getAllCodeBlocksByUserId(TEST_USER_UUID);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void getAllCodeBlocksByType_codeBlockEntityExist_shodReturnNotEmptyCodeBlockEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        given(codeBlockRepository.findAllByType(CodeBlockType.PRIVATE)).willReturn(expected);

        List<CodeBlockEntity> actual = codeBlockService.getAllCodeBlocksByType(CodeBlockType.PRIVATE);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void getAllFilteredCodeBlocks_codeBlockEntityExist_allFiltersOff_shodReturnEmptyList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        String filterQuery = "query";
        codeBlockEntity.setTitle(filterQuery);
        codeBlockEntity.setDescription(filterQuery);
        codeBlockEntity.setContent(filterQuery);
        FilterCodeBlockDto filterCodeBlockDto = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOff();
        filterCodeBlockDto.setFilterQuery(filterQuery);
        List<CodeBlockEntity> codeBlockEntityList = List.of(codeBlockEntity);

        given(codeBlockRepository.findAllByOrderByUpdatedDesc()).willReturn(codeBlockEntityList);

        List<CodeBlockEntity> actual = codeBlockService.getAllFilteredCodeBlocks(filterCodeBlockDto);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void getAllFilteredCodeBlocks_codeBlockEntityExist_emptyFilterQuery_shodReturnNotEmptyList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        String filterQuery = "";
        FilterCodeBlockDto filterCodeBlockDto = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOff();
        filterCodeBlockDto.setFilterQuery(filterQuery);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        given(codeBlockRepository.findAllByOrderByUpdatedDesc()).willReturn(expected);

        List<CodeBlockEntity> actual = codeBlockService.getAllFilteredCodeBlocks(filterCodeBlockDto);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void getAllFilteredCodeBlocksByUserId_codeBlockEntityExist_shodReturnNotEmptyList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        String filterQuery = "";
        FilterCodeBlockDto filterCodeBlockDto = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOff();
        filterCodeBlockDto.setFilterQuery(filterQuery);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        given(codeBlockRepository.findAllByUserIdOrderByUpdatedDesc(TEST_USER_UUID)).willReturn(expected);

        List<CodeBlockEntity> actual = codeBlockService
                .getAllFilteredCodeBlocksByUserId(TEST_USER_UUID, filterCodeBlockDto);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void getAllFilteredCodeBlocksByUserIdAndEstimateType_codeBlockEntityExist_shodReturnNotEmptyList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        String filterQuery = "";
        FilterCodeBlockDto filterCodeBlockDto = DtoExampleStorage.getFilterCodeBlockDtoAllFiltersOff();
        filterCodeBlockDto.setFilterQuery(filterQuery);
        List<CodeBlockEntity> expected = List.of(codeBlockEntity);

        given(codeBlockRepository.findAllCodeBlocksByUserIdAndEstimateType(
                TEST_USER_UUID, EstimateType.LIKE)).willReturn(expected);

        List<CodeBlockEntity> actual = codeBlockService.getAllFilteredCodeBlocksByUserIdAndEstimateType(
                TEST_USER_UUID, EstimateType.LIKE, filterCodeBlockDto);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }
}
