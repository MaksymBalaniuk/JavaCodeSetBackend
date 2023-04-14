package com.javacodeset.service.api;

import com.javacodeset.dto.permission.UserPermissionsDto;

import java.util.UUID;

public interface AuthorityService {
    Boolean isUserHasAdminAuthority(UUID userId);
    UserPermissionsDto getUserPermissions(UUID userId);
}
