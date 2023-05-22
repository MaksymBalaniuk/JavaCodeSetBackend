package com.javacodeset.example;

import com.javacodeset.entity.*;
import com.javacodeset.enumeration.EstimateType;

public final class EntityExampleStorage {

    public static AuthorityEntity getAuthorityEntity() {
        AuthorityEntity authorityEntity = new AuthorityEntity();
        authorityEntity.setName("ROLE_SOME");
        return authorityEntity;
    }

    public static UserEntity getUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("username");
        userEntity.setPassword("12345678");
        userEntity.setEmail("username@gmail.com");
        return userEntity;
    }

    public static CodeBlockEntity getCodeBlockEntity(UserEntity userEntity) {
        CodeBlockEntity codeBlockEntity = new CodeBlockEntity();
        codeBlockEntity.setUser(userEntity);
        return codeBlockEntity;
    }

    public static EstimateEntity getEstimateEntity(UserEntity userEntity, CodeBlockEntity codeBlockEntity) {
        EstimateEntity estimateEntity = new EstimateEntity();
        estimateEntity.setUser(userEntity);
        estimateEntity.setCodeBlock(codeBlockEntity);
        estimateEntity.setType(EstimateType.LIKE);
        return estimateEntity;
    }

    public static ShareEntity getShareEntity(UserEntity toUser, UserEntity fromUser, CodeBlockEntity codeBlock) {
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.setToUser(toUser);
        shareEntity.setFromUser(fromUser);
        shareEntity.setCodeBlock(codeBlock);
        return shareEntity;
    }

    public static CommentEntity getCommentEntity(UserEntity userEntity, CodeBlockEntity codeBlockEntity) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setUser(userEntity);
        commentEntity.setCodeBlock(codeBlockEntity);
        return commentEntity;
    }
}
