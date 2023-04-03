package com.javacodeset.service.api;

import com.javacodeset.dto.permission.UserPermissionsDto;
import com.javacodeset.entity.AuthorityEntity;

import java.util.UUID;

public interface AuthorityService {
    AuthorityEntity getAuthorityByName(String authorityName);
    UserPermissionsDto getUserPermissions(UUID userId);
}
