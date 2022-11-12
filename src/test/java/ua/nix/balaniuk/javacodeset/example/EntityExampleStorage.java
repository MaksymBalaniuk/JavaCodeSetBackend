package ua.nix.balaniuk.javacodeset.example;

import ua.nix.balaniuk.javacodeset.entity.*;
import ua.nix.balaniuk.javacodeset.enumeration.EstimateType;

public final class EntityExampleStorage {

    public static UserEntity getUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("maxim");
        userEntity.setPassword("12345");
        userEntity.setEmail("maxim@gmail.com");
        return userEntity;
    }

    public static CodeBlockEntity getCodeBlockEntity(UserEntity userEntity) {
        CodeBlockEntity codeBlockEntity = new CodeBlockEntity();
        codeBlockEntity.setUser(userEntity);
        return codeBlockEntity;
    }

    public static CommentEntity getCommentEntity(UserEntity userEntity, CodeBlockEntity codeBlockEntity) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setUser(userEntity);
        commentEntity.setCodeBlock(codeBlockEntity);
        return commentEntity;
    }

    public static EstimateEntity getEstimateEntity(UserEntity userEntity, CodeBlockEntity codeBlockEntity) {
        EstimateEntity estimateEntity = new EstimateEntity();
        estimateEntity.setUser(userEntity);
        estimateEntity.setCodeBlock(codeBlockEntity);
        estimateEntity.setType(EstimateType.LIKE);
        return estimateEntity;
    }

    public static TagEntity getTagEntity() {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName("#stream");
        return tagEntity;
    }

    public static AuthorityEntity getAuthorityEntity() {
        AuthorityEntity authorityEntity = new AuthorityEntity();
        authorityEntity.setName("ROLE_SOME");
        return authorityEntity;
    }
}
