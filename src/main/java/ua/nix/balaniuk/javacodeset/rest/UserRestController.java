package ua.nix.balaniuk.javacodeset.rest;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import ua.nix.balaniuk.javacodeset.dto.UserDto;
import ua.nix.balaniuk.javacodeset.dto.auth.AuthenticationResponseDto;
import ua.nix.balaniuk.javacodeset.dto.premium.PremiumLimitsDto;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.UserPremium;
import ua.nix.balaniuk.javacodeset.security.jwt.JwtTokenProvider;
import ua.nix.balaniuk.javacodeset.service.api.UserService;
import ua.nix.balaniuk.javacodeset.util.PremiumLimitsPolicy;
import ua.nix.balaniuk.javacodeset.util.UserResponseCredentialsHidingPolicy;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/get/{userId}")
    public UserDto getUserById(@PathVariable UUID userId) {
        return UserResponseCredentialsHidingPolicy.hide(modelMapper.map(userService.get(userId), UserDto.class));
    }

    @PatchMapping("/update/{userId}/username/{username}")
    public AuthenticationResponseDto updateUsernameById(@PathVariable UUID userId, @PathVariable String username) {
        UserEntity user = userService.updateUsername(userId, username);
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getAuthorities());
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
