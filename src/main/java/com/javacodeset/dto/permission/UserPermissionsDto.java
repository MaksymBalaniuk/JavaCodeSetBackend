package com.javacodeset.dto.permission;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPermissionsDto {
    private Boolean publicStorageManagementPermission;
    private Boolean viewProfilePermission;
    private Boolean userBanPermission;
    private Boolean contentHidePermission;
}
