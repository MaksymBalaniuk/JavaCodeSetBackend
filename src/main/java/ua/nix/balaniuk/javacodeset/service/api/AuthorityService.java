package ua.nix.balaniuk.javacodeset.service.api;

import ua.nix.balaniuk.javacodeset.dto.permission.UserPermissionsDto;
import ua.nix.balaniuk.javacodeset.entity.AuthorityEntity;

import java.util.UUID;

public interface AuthorityService {
    AuthorityEntity getByName(String authorityName);
    UserPermissionsDto getUserPermissions(UUID userId);
}
