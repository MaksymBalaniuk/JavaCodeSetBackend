package com.javacodeset.util;

import com.javacodeset.dto.permission.UserPermissionsDto;

import java.util.List;

public abstract class UserPermissionsPolicy {

    private UserPermissionsPolicy() {
    }

    public static UserPermissionsDto getUserPermissions(List<String> authorities) {
        return UserPermissionsDto.builder()
                .publicStorageManagementPermission(authorities.contains("ROLE_ADMIN"))
                .viewProfilePermission(authorities.contains("ROLE_ADMIN"))
                .userBanPermission(authorities.contains("ROLE_ADMIN"))
                .contentHidePermission(authorities.contains("ROLE_DEVELOPER"))
                .build();
    }
}
