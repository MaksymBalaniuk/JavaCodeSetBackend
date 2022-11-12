package ua.nix.balaniuk.javacodeset.util;

import ua.nix.balaniuk.javacodeset.dto.permission.UserPermissionsDto;

import java.util.List;

public abstract class UserPermissionsPolicy {

    private UserPermissionsPolicy() {
    }

    public static UserPermissionsDto getUserPermissions(List<String> authorities) {
        UserPermissionsDto userPermissionsDto = new UserPermissionsDto();
        userPermissionsDto.setPublicStorageManagementPermission(authorities.contains("ROLE_ADMIN"));
        userPermissionsDto.setViewProfilePermission(authorities.contains("ROLE_ADMIN"));
        userPermissionsDto.setUserBanPermission(authorities.contains("ROLE_ADMIN"));
        userPermissionsDto.setContentHidePermission(authorities.contains("ROLE_DEVELOPER"));
        return userPermissionsDto;
    }
}
