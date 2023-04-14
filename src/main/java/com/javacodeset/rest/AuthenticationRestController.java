package com.javacodeset.rest;

import com.javacodeset.dto.UserDto;
import com.javacodeset.security.userdetails.JwtUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import com.javacodeset.dto.auth.AuthenticationRequestDto;
import com.javacodeset.dto.auth.AuthenticationResponseDto;
import com.javacodeset.dto.auth.RegisterRequestDto;
import com.javacodeset.dto.auth.RegisterResponseDto;
import com.javacodeset.service.api.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final ModelMapper modelMapper;

    @PostMapping("/login")
    public AuthenticationResponseDto login(@RequestBody AuthenticationRequestDto requestDto) {
        return authenticationService.login(requestDto);
    }

    @PostMapping("/register")
    public RegisterResponseDto register(@RequestBody RegisterRequestDto requestDto) {
        return authenticationService.register(requestDto);
    }

    @GetMapping("/get/authenticated-user")
    public UserDto getAuthenticatedUser() {
        return modelMapper.map(jwtUserDetailsService.getAuthenticatedUser(), UserDto.class);
    }
}
