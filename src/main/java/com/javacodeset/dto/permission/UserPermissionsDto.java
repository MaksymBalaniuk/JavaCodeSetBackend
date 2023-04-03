package com.javacodeset.dto.permission;

import lombok.Data;

@Data
public class UserPermissionsDto {
    private Boolean publicStorageManagementPermission;
    private Boolean viewProfilePermission;
    private Boolean userBanPermission;
    private Boolean contentHidePermission;
}
