package com.javacodeset.example;

import com.javacodeset.entity.*;
import com.javacodeset.enumeration.EstimateType;

public final class EntityExampleStorage {

    public static AuthorityEntity getAuthorityEntity(String name) {
        AuthorityEntity authorityEntity = new AuthorityEntity();
        authorityEntity.setName(name);
        return authorityEntity;
    }

    public static UserEntity getUserEntity(String username, String password, String email) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(password);
        userEntity.setEmail(email);
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

    public static TagEntity getTagEntity(String name) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(name);
        return tagEntity;
    }
}
