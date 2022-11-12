package ua.nix.balaniuk.javacodeset.service.api;

import ua.nix.balaniuk.javacodeset.dto.UserDto;
import ua.nix.balaniuk.javacodeset.dto.premium.PremiumLimitsDto;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;

import java.util.UUID;

public interface UserService extends CrudOperationsService<UserEntity, UserDto, UUID> {
    UserEntity getByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    UserEntity updateUsername(UUID userId, String username);
    UserEntity activateUserById(UUID userId);
    UserEntity banUserById(UUID userId);
    UserEntity markUserDeletedById(UUID userId);
    PremiumLimitsDto getUserPremiumLimits(UUID userId);
}
