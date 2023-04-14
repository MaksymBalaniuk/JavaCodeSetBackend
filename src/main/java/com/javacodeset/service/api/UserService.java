package com.javacodeset.service.api;

import com.javacodeset.dto.UserDto;
import com.javacodeset.dto.premium.PremiumLimitsDto;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.UserPremium;

import java.util.List;
import java.util.UUID;

public interface UserService extends CrudService<UserEntity, UserDto, UUID> {
    UserEntity getUserByUsername(String username);
    List<UserEntity> searchUsersByUsername(String username);
    Boolean existsUserByUsername(String username);
    Boolean existsUserByEmail(String email);
    UserEntity updateUserUsername(UUID userId, String username);
    UserEntity updateUserPremium(UUID userId, UserPremium userPremium);
    UserEntity activateUserById(UUID userId);
    UserEntity banUserById(UUID userId);
    UserEntity markUserDeletedById(UUID userId);
    PremiumLimitsDto getUserPremiumLimits(UUID userId);
}
