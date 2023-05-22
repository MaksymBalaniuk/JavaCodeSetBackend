package com.javacodeset.example;

import com.javacodeset.entity.AuthorityEntity;

public final class EntityExampleStorage {

    public static AuthorityEntity getAuthorityEntity() {
        AuthorityEntity authorityEntity = new AuthorityEntity();
        authorityEntity.setName("ROLE_SOME");
        return authorityEntity;
    }
}
