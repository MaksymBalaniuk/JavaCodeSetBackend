package com.javacodeset.rest;

import com.javacodeset.security.userdetails.JwtUserDetailsService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final ModelMapper modelMapper;
    private final JwtProvider jwtProvider;

    @GetMapping("/get/{userId}")
    public UserDto getUserById(@PathVariable UUID userId) {
        return UserResponseCredentialsHidingPolicy.hide(
                modelMapper.map(userService.get(userId), UserDto.class));
    }

    @GetMapping("/get/by-username/{username}")
    public UserDto getUserByUsername(@PathVariable String username) {
        return UserResponseCredentialsHidingPolicy.hide(
                modelMapper.map(userService.getUserByUsername(username), UserDto.class));
    }

    @GetMapping("/get-all/by-username/{username}")
    public List<UserDto> searchUsersByUsername(@PathVariable String username) {
        return userService.searchUsersByUsername(username).stream()
                .map(userEntity -> UserResponseCredentialsHidingPolicy.hide(
                        modelMapper.map(userEntity, UserDto.class))).toList();
    }

    @PatchMapping("/update/authenticated-user/username/{username}")
    public AuthenticationResponseDto updateAuthenticatedUserUsername(@PathVariable String username) {
        UserEntity user = jwtUserDetailsService.updateAuthenticatedUserUsername(username);
        String token = jwtProvider.createToken(user.getUsername(), user.getAuthorities());
        return new AuthenticationResponseDto(token, user.getId());
    }

    @PatchMapping("/update/authenticated-user/email/{email}")
    public AuthenticationResponseDto updateAuthenticatedUserEmail(@PathVariable String email) {
        UserEntity user = jwtUserDetailsService.updateAuthenticatedUserEmail(email);
        String token = jwtProvider.createToken(user.getUsername(), user.getAuthorities());
        return new AuthenticationResponseDto(token, user.getId());
    }

    @PatchMapping("/update/{userId}/user-premium/{userPremium}")
    public AuthenticationResponseDto updateUserPremiumById(
            @PathVariable UUID userId, @PathVariable UserPremium userPremium) {
        UserEntity user = userService.updateUserPremium(userId, userPremium);
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
