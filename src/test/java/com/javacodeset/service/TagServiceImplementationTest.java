package com.javacodeset.service;

import com.javacodeset.dto.TagDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.TagEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.example.DtoExampleStorage;
import com.javacodeset.example.EntityExampleStorage;
import com.javacodeset.exception.BadRequestException;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.exception.ProhibitedOperationException;
import com.javacodeset.repository.CodeBlockRepository;
import com.javacodeset.repository.TagRepository;
import com.javacodeset.service.impl.TagServiceImplementation;
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
    public void create_validData_shouldReturnSavedTagEntity() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity("#stream");
        TagDto tagDto = DtoExampleStorage.getTagDto("#stream");

        given(tagRepository.existsByName(tagDto.getName())).willReturn(false);
        given(modelMapper.map(tagDto, TagEntity.class)).willReturn(tagEntity);
        given(tagRepository.save(tagEntity)).willReturn(tagEntity);

        TagEntity actual = tagService.create(tagDto);

        assertNotNull(actual);

        verify(tagRepository).save(any(TagEntity.class));
    }

    @Test(expected = BadRequestException.class)
    public void create_emptyName_shouldThrowException() {
        TagDto tagDto = DtoExampleStorage.getTagDto("");
        tagService.create(tagDto);
    }

    @Test
    public void create_tagWithInputNameExist_shouldReturnExistTag() {
        TagEntity expected = EntityExampleStorage.getTagEntity("#stream");
        TagDto tagDto = DtoExampleStorage.getTagDto("#stream");

        given(tagRepository.existsByName(tagDto.getName())).willReturn(true);
        given(tagRepository.findByName(tagDto.getName())).willReturn(Optional.of(expected));

        TagEntity actual = tagService.create(tagDto);

        assertEquals(expected, actual);
    }

    @Test
    public void get_validId_shouldReturnTagEntity() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity("#stream");
        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.of(tagEntity));
        TagEntity actual = tagService.get(TEST_TAG_UUID);
        assertNotNull(actual);
    }

    @Test(expected = NotFoundException.class)
    public void get_invalidId_shouldThrowException() {
        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.empty());
        tagService.get(TEST_TAG_UUID);
    }

    @Test
    public void getAll_shouldReturnTagEntityList() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity("#stream");
        List<TagEntity> expected = List.of(tagEntity);

        given(tagRepository.findAll()).willReturn(expected);

        List<TagEntity> actual = tagService.getAll();

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test(expected = ProhibitedOperationException.class)
    public void update_shouldThrowException() {
        TagDto tagDto = DtoExampleStorage.getTagDto("#stream");
        tagService.update(tagDto);
    }

    @Test
    public void delete_tagEntityExist_shouldDeleteTagEntity() {
        given(tagRepository.existsById(TEST_TAG_UUID)).willReturn(true);
        willDoNothing().given(tagRepository).deleteById(TEST_TAG_UUID);
        tagService.delete(TEST_TAG_UUID);
        verify(tagRepository).deleteById(any(UUID.class));
    }

    @Test
    public void getTagByName_tagEntityExist_shouldReturnTagEntity() {
        TagEntity expected = EntityExampleStorage.getTagEntity("#stream");
        given(tagRepository.findByName(expected.getName())).willReturn(Optional.of(expected));
        TagEntity actual = tagService.getTagByName(expected.getName());
        assertEquals(expected, actual);
    }

    @Test(expected = NotFoundException.class)
    public void getTagByName_tagEntityNotExist_shouldThrowException() {
        String tagName = "#stream";
        given(tagRepository.findByName(tagName)).willReturn(Optional.empty());
        tagService.getTagByName(tagName);
    }

    @Test
    public void existsTagByName_shouldReturnBoolean() {
        String tagName = "#stream";
        given(tagRepository.existsByName(tagName)).willReturn(true);
        Boolean actual = tagService.existsTagByName(tagName);
        assertTrue(actual);
    }

    @Test
    public void getAllTagsByCodeBlockId_tagEntityExist_shouldReturnNotEmptyTagEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        TagEntity tagEntity = EntityExampleStorage.getTagEntity("#stream");
        codeBlockEntity.getTags().add(tagEntity);
        List<TagEntity> expected = List.of(tagEntity);

        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.of(codeBlockEntity));

        List<TagEntity> actual = tagService.getAllTagsByCodeBlockId(TEST_CODE_BLOCK_UUID);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void getAllTagsByCodeBlockId_tagEntityNotExist_shouldReturnEmptyTagEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);

        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.of(codeBlockEntity));

        List<TagEntity> actual = tagService.getAllTagsByCodeBlockId(TEST_CODE_BLOCK_UUID);

        assertTrue(actual.isEmpty());
    }

    @Test(expected = NotFoundException.class)
    public void getAllTagsByCodeBlockId_invalidId_shouldThrowException() {
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.empty());
        tagService.getAllTagsByCodeBlockId(TEST_CODE_BLOCK_UUID);
    }

    @Test
    public void addTagToCodeBlock_validData_shouldAddTagEntityToCodeBlockEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        TagEntity tagEntity = EntityExampleStorage.getTagEntity("#stream");

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
    public void addTagToCodeBlock_invalidTagId_shouldThrowException() {
        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.empty());
        tagService.addTagToCodeBlock(TEST_TAG_UUID, TEST_CODE_BLOCK_UUID);
    }

    @Test(expected = NotFoundException.class)
    public void addTagToCodeBlock_invalidCodeBlockId_shouldThrowException() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity("#stream");
        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.of(tagEntity));
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.empty());
        tagService.addTagToCodeBlock(TEST_TAG_UUID, TEST_CODE_BLOCK_UUID);
    }

    @Test
    public void deleteTagFromCodeBlock_validData_shouldDeleteTagEntityFromCodeBlockEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity("1", "1", "1@gmail.com");
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        TagEntity tagEntity = EntityExampleStorage.getTagEntity("#stream");
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
    public void deleteTagFromCodeBlock_invalidTagId_shouldThrowException() {
        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.empty());
        tagService.deleteTagFromCodeBlock(TEST_TAG_UUID, TEST_CODE_BLOCK_UUID);
    }

    @Test(expected = NotFoundException.class)
    public void deleteTagFromCodeBlock_invalidCodeBlockId_shouldThrowException() {
        TagEntity tagEntity = EntityExampleStorage.getTagEntity("#stream");
        given(tagRepository.findById(TEST_TAG_UUID)).willReturn(Optional.of(tagEntity));
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.empty());
        tagService.deleteTagFromCodeBlock(TEST_TAG_UUID, TEST_CODE_BLOCK_UUID);
    }
}
