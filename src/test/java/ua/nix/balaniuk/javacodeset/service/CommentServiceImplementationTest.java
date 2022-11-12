package ua.nix.balaniuk.javacodeset.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import ua.nix.balaniuk.javacodeset.dto.CommentDto;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.CommentEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.example.DtoExampleStorage;
import ua.nix.balaniuk.javacodeset.example.EntityExampleStorage;
import ua.nix.balaniuk.javacodeset.exception.NotFoundException;
import ua.nix.balaniuk.javacodeset.repository.CodeBlockRepository;
import ua.nix.balaniuk.javacodeset.repository.CommentRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.service.impl.CommentServiceImplementation;

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
public class CommentServiceImplementationTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CodeBlockRepository codeBlockRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentServiceImplementation commentService;

    private static final UUID TEST_COMMENT_UUID = UUID.randomUUID();
    private static final UUID TEST_USER_UUID = UUID.randomUUID();
    private static final UUID TEST_CODE_BLOCK_UUID = UUID.randomUUID();

    @Test
    public void create_validData_shodReturnSavedCommentEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        CommentEntity commentEntity = EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity);
        CommentDto commentDto = DtoExampleStorage.getCommentDto(TEST_USER_UUID, TEST_CODE_BLOCK_UUID);

        given(userRepository.findById(commentDto.getUserId())).willReturn(Optional.of(userEntity));
        given(codeBlockRepository.findById(commentDto.getCodeBlockId())).willReturn(Optional.of(codeBlockEntity));
        given(modelMapper.map(commentDto, CommentEntity.class)).willReturn(commentEntity);
        given(commentRepository.save(commentEntity)).willReturn(commentEntity);
        given(userRepository.save(userEntity)).willReturn(userEntity);
        given(codeBlockRepository.save(codeBlockEntity)).willReturn(codeBlockEntity);

        CommentEntity actual = commentService.create(commentDto);

        assertNotNull(actual);

        verify(commentRepository).save(any(CommentEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void create_invalidUserId_shodThrowException() {
        CommentDto commentDto = DtoExampleStorage.getCommentDto(TEST_USER_UUID, TEST_CODE_BLOCK_UUID);
        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.empty());
        commentService.create(commentDto);
    }

    @Test(expected = NotFoundException.class)
    public void create_invalidCodeBlockId_shodThrowException() {
        CommentDto commentDto = DtoExampleStorage.getCommentDto(TEST_USER_UUID, TEST_CODE_BLOCK_UUID);
        UserEntity userEntity = EntityExampleStorage.getUserEntity();

        given(userRepository.findById(TEST_USER_UUID)).willReturn(Optional.of(userEntity));
        given(codeBlockRepository.findById(TEST_CODE_BLOCK_UUID)).willReturn(Optional.empty());

        commentService.create(commentDto);
    }

    @Test
    public void get_validId_shodReturnCommentEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        CommentEntity commentEntity = EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity);

        given(commentRepository.findById(TEST_COMMENT_UUID)).willReturn(Optional.of(commentEntity));

        CommentEntity actual = commentService.get(TEST_COMMENT_UUID);

        assertNotNull(actual);
    }

    @Test(expected = NotFoundException.class)
    public void get_invalidId_shodThrowException() {
        given(commentRepository.findById(TEST_COMMENT_UUID)).willReturn(Optional.empty());
        commentService.get(TEST_COMMENT_UUID);
    }

    @Test
    public void getAll_shodReturnCommentEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        CommentEntity commentEntity = EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity);
        List<CommentEntity> expected = List.of(commentEntity);

        given(commentRepository.findAll()).willReturn(expected);

        List<CommentEntity> actual = commentService.getAll();

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void update_validData_shodReturnUpdatedCommentEntity() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        CommentEntity commentEntity = EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity);
        commentEntity.setComment("");
        CommentDto commentDto = DtoExampleStorage.getCommentDto(TEST_USER_UUID, TEST_CODE_BLOCK_UUID);
        commentDto.setId(TEST_COMMENT_UUID);
        commentDto.setComment("comment");

        given(commentRepository.findById(TEST_COMMENT_UUID)).willReturn(Optional.of(commentEntity));
        given(commentRepository.save(commentEntity)).willReturn(commentEntity);

        CommentEntity actual = commentService.update(commentDto);

        assertEquals(commentDto.getComment(), actual.getComment());

        verify(commentRepository).save(any(CommentEntity.class));
    }

    @Test(expected = NotFoundException.class)
    public void update_invalidCommentId_shodThrowException() {
        CommentDto commentDto = DtoExampleStorage.getCommentDto(TEST_USER_UUID, TEST_CODE_BLOCK_UUID);
        commentDto.setId(TEST_COMMENT_UUID);
        given(commentRepository.findById(commentDto.getId())).willReturn(Optional.empty());
        commentService.update(commentDto);
    }

    @Test
    public void delete_commentEntityExist_shodDeleteCommentEntity() {
        given(commentRepository.existsById(TEST_COMMENT_UUID)).willReturn(true);
        willDoNothing().given(commentRepository).deleteById(TEST_COMMENT_UUID);
        commentService.delete(TEST_COMMENT_UUID);
        verify(commentRepository).deleteById(any(UUID.class));
    }

    @Test
    public void getAllCommentsByCodeBlockId_commentEntityExist_shodReturnEmptyCommentEntityList() {
        UserEntity userEntity = EntityExampleStorage.getUserEntity();
        CodeBlockEntity codeBlockEntity = EntityExampleStorage.getCodeBlockEntity(userEntity);
        CommentEntity commentEntity = EntityExampleStorage.getCommentEntity(userEntity, codeBlockEntity);
        List<CommentEntity> expected = List.of(commentEntity);

        given(commentRepository.findAllByCodeBlockIdOrderByUpdatedDesc(TEST_CODE_BLOCK_UUID)).willReturn(expected);

        List<CommentEntity> actual = commentService.getAllCommentsByCodeBlockId(TEST_CODE_BLOCK_UUID);

        assertThat(expected, containsInAnyOrder(actual.toArray()));
    }
}
