package com.javacodeset.rest;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import com.javacodeset.dto.UserDto;
import com.javacodeset.dto.auth.AuthenticationResponseDto;
import com.javacodeset.dto.premium.PremiumLimitsDto;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.enumeration.UserPremium;
import com.javacodeset.security.jwt.JwtProvider;
import com.javacodeset.service.api.UserService;
import com.javacodeset.util.PremiumLimitsPolicy;
import com.javacodeset.util.UserResponseCredentialsHidingPolicy;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JwtProvider jwtProvider;

    @GetMapping("/get/{userId}")
    public UserDto getUserById(@PathVariable UUID userId) {
        return UserResponseCredentialsHidingPolicy.hide(
                modelMapper.map(userService.get(userId), UserDto.class));
    }

    @PatchMapping("/update/{userId}/username/{username}")
    public AuthenticationResponseDto updateUserUsernameById(@PathVariable UUID userId, @PathVariable String username) {
        UserEntity user = userService.updateUserUsername(userId, username);
        String token = jwtProvider.createToken(user.getUsername(), user.getAuthorities());
        return new AuthenticationResponseDto(token, user.getId());
    }

    @PatchMapping("/update/{userId}/activate")
    public UserDto activateUserById(@PathVariable UUID userId) {
        return UserResponseCredentialsHidingPolicy.hide(
                modelMapper.map(userService.activateUserById(userId), UserDto.class));
    }

    @PatchMapping("/update/{userId}/ban")
    public UserDto banUserById(@PathVariable UUID userId) {
        return UserResponseCredentialsHidingPolicy.hide(
                modelMapper.map(userService.banUserById(userId), UserDto.class));
    }

    @PatchMapping("/update/{userId}/mark-deleted")
    public UserDto markUserDeletedById(@PathVariable UUID userId) {
        return UserResponseCredentialsHidingPolicy.hide(
                modelMapper.map(userService.markUserDeletedById(userId), UserDto.class));
    }

    @GetMapping("/get/{userId}/premium-limits")
    public PremiumLimitsDto getUserPremiumLimits(@PathVariable UUID userId) {
        return userService.getUserPremiumLimits(userId);
    }

    @GetMapping("/get/premium-limits/by-user-premium/{userPremium}")
    public PremiumLimitsDto getPremiumLimitsByUserPremium(@PathVariable UserPremium userPremium) {
        return PremiumLimitsPolicy.getPremiumLimits(userPremium);
    }
}
