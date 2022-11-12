package ua.nix.balaniuk.javacodeset.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import ua.nix.balaniuk.javacodeset.dto.TagDto;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.TagEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.example.DtoExampleStorage;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;
import ua.nix.balaniuk.javacodeset.exception.BadRequestException;
import ua.nix.balaniuk.javacodeset.exception.NotFoundException;
import ua.nix.balaniuk.javacodeset.exception.ProhibitedOperationException;
import ua.nix.balaniuk.javacodeset.repository.CodeBlockRepository;
import ua.nix.balaniuk.javacodeset.repository.TagRepository;
import ua.nix.balaniuk.javacodeset.service.impl.TagServiceImplementation;

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
public class TagServiceImplementationTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CodeBlockRepository codeBlockRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TagServiceImplementation tagService;

    private static final UUID TEST_TAG_UUID = UUID.randomUUID();
    private static final UUID TEST_CODE_BLOCK_UUID = UUID.randomUUID();

    @Test
    public void create_validData_shodReturnSavedTagEntity() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();
        TagDto tagDto = DtoExampleStorage.getTagDto();

        given(tagRepository.existsByName(tagDto.getName())).willReturn(false);
        given(modelMapper.map(tagDto, TagEntity.class)).willReturn(tagEntity);
        given(tagRepository.save(tagEntity)).willReturn(tagEntity);

        TagEntity actual = tagService.create(tagDto);

        assertNotNull(actual);

        verify(tagRepository).save(any(TagEntity.class));
    }

    @Test(expected = BadRequestException.class)
    public void create_emptyName_shodThrowException() {
        TagDto tagDto = DtoExampleStorage.getTagDto();
        tagDto.setName("");
        tagService.create(tagDto);
    }

    @Test
    public void create_nameValidation_shodReturnSavedTagEntity() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();
        TagDto tagDto = DtoExampleStorage.getTagDto();
        tagDto.setName("stream");

        given(modelMapper.map(tagDto, TagEntity.class)).willReturn(tagEntity);
        given(tagRepository.save(tagEntity)).willReturn(tagEntity);

        TagEntity actual = tagService.create(tagDto);

        assertNotNull(actual);

        verify(tagRepository).save(any(TagEntity.class));
    }

    @Test
    public void create_tagWithInputNameExist_shodReturnExistTag() {
        TagEntity expected = EntityExampleStorage.getTagEntity();
        TagDto tagDto = DtoExampleStorage.getTagDto();

        given(tagRepository.existsByName(tagDto.getName())).willReturn(true);
        given(tagRepository.findByName(tagDto.getName())).willReturn(Optional.of(expected));

        TagEntity actual = tagService.create(tagDto);

        assertEquals(expected, actual);
    }

    @Test(expected = NotFoundException.class)
    public void create_tagWithInputNameExist_butNotFound_shodThrowException() {
        TagDto tagDto = DtoExampleStorage.getTagDto();
        given(tagRepository.existsByName(tagDto.getName())).willReturn(true);
        given(tagRepository.findByName(tagDto.getName())).willReturn(Optional.empty());
        tagService.create(tagDto);
    }

    @Test
    public void get_validId_shodReturnTagEntity() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();
        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.of(tagEntity));
        TagEntity actual = tagService.get(TEST_TAG_UUID);
        assertNotNull(actual);
    }

    @Test(expected = NotFoundException.class)
    public void get_invalidId_shodThrowException() {
        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.empty());
        tagService.get(TEST_TAG_UUID);
    }

    @Test
    public void getAll_shodReturnTagEntityList() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();
        List<TagEntity> expected = List.of(tagEntity);

        given(tagRepository.findAll()).willReturn(expected);

        List<TagEntity> actual = tagService.getAll();

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test(expected = ProhibitedOperationException.class)
    public void update_shodThrowException() {
        TagDto tagDto = DtoExampleStorage.getTagDto();
        tagService.update(tagDto);
    }

    @Test
    public void delete_tagEntityExist_shodDeleteTagEntity() {
        given(tagRepository.existsById(TEST_TAG_UUID)).willReturn(true);
        willDoNothing().given(tagRepository).deleteById(TEST_TAG_UUID);
        tagService.delete(TEST_TAG_UUID);
        verify(tagRepository).deleteById(any(UUID.class));
    }

    @Test
    public void getByName_tagEntityExist_shodReturnTagEntity() {
        TagEntity expected = EntityExampleStorage.getTagEntity();
        given(tagRepository.findByName(expected.getName())).willReturn(Optional.of(expected));
        TagEntity actual = tagService.getByName(expected.getName());
        assertEquals(expected, actual);
    }

    @Test(expected = NotFoundException.class)
    public void getByName_tagEntityNotExist_shodThrowException() {
        String tagName = "#stream";
        given(tagRepository.findByName(tagName)).willReturn(Optional.empty());
        tagService.getByName(tagName);
    }

    @Test
    public void existByName_shodReturnBoolean() {
        String tagName = "#stream";
        given(tagRepository.existsByName(tagName)).willReturn(true);
        boolean actual = tagService.existByName(tagName);
        assertTrue(actual);
    }

    @Test
    public void getAllTagsByCodeBlockId_tagEntityExist_shodReturnNotEmptyTagEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();
        codeBlockEntity.getTags().add(tagEntity);
        List<TagEntity> expected = List.of(tagEntity);

        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.of(codeBlockEntity));

        List<TagEntity> actual = tagService.getAllTagsByCodeBlockId(TEST_CODE_BLOCK_UUID);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void getAllTagsByCodeBlockId_tagEntityNotExist_shodReturnEmptyTagEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);

        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.of(codeBlockEntity));

        List<TagEntity> actual = tagService.getAllTagsByCodeBlockId(TEST_CODE_BLOCK_UUID);

        assertTrue(actual.isEmpty());
    }

    @Test(expected = NotFoundException.class)
    public void getAllTagsByCodeBlockId_invalidId_shodThrowException() {
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.empty());
        tagService.getAllTagsByCodeBlockId(TEST_CODE_BLOCK_UUID);
    }

    @Test
    public void addTagToCodeBlock_validData_shodAddTagEntityToCodeBlockEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();

        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.of(tagEntity));
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.of(codeBlockEntity));
        given(tagRepository.save(tagEntity)).willReturn(tagEntity);
        given(codeBlockRepository.save(codeBlockEntity)).willReturn(codeBlockEntity);

        tagService.addTagToCodeBlock(TEST_TAG_UUID, TEST_CODE_BLOCK_UUID);

        assertTrue(tagEntity.getCodeBlocks().contains(codeBlockEntity));
        assertTrue(codeBlockEntity.getTags().contains(tagEntity));

        verify(tagRepository).save(any(TagEntity.class));
        verify(codeBlockRepository).save(any(CodeBlockEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void addTagToCodeBlock_invalidTagId_shodThrowException() {
        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.empty());
        tagService.addTagToCodeBlock(TEST_TAG_UUID, TEST_CODE_BLOCK_UUID);
    }

    @Test(expected = NotFoundException.class)
    public void addTagToCodeBlock_invalidCodeBlockId_shodThrowException() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();
        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.of(tagEntity));
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.empty());
        tagService.addTagToCodeBlock(TEST_TAG_UUID, TEST_CODE_BLOCK_UUID);
    }

    @Test
    public void deleteTagFromCodeBlock_validData_shodDeleteTagEntityFromCodeBlockEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();
        tagEntity.getCodeBlocks().add(codeBlockEntity);
        codeBlockEntity.getTags().add(tagEntity);

        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.of(tagEntity));
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.of(codeBlockEntity));
        given(tagRepository.save(tagEntity)).willReturn(tagEntity);
        given(codeBlockRepository.save(codeBlockEntity)).willReturn(codeBlockEntity);

        tagService.deleteTagFromCodeBlock(TEST_TAG_UUID, TEST_CODE_BLOCK_UUID);

        assertFalse(tagEntity.getCodeBlocks().contains(codeBlockEntity));
        assertFalse(codeBlockEntity.getTags().contains(tagEntity));

        verify(tagRepository).save(any(TagEntity.class));
        verify(codeBlockRepository).save(any(CodeBlockEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void deleteTagFromCodeBlock_invalidTagId_shodThrowException() {
        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.empty());
        tagService.deleteTagFromCodeBlock(TEST_TAG_UUID, TEST_CODE_BLOCK_UUID);
    }

    @Test(expected = NotFoundException.class)
    public void deleteTagFromCodeBlock_invalidCodeBlockId_shodThrowException() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity();
        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.of(tagEntity));
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.empty());
        tagService.deleteTagFromCodeBlock(TEST_TAG_UUID, TEST_CODE_BLOCK_UUID);
    }
}
