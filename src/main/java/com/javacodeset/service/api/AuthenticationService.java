package com.javacodeset.service.api;

import com.javacodeset.dto.auth.AuthenticationRequestDto;
import com.javacodeset.dto.auth.AuthenticationResponseDto;
import com.javacodeset.dto.auth.RegisterRequestDto;
import com.javacodeset.dto.auth.RegisterResponseDto;

public interface AuthenticationService {
    AuthenticationResponseDto login(AuthenticationRequestDto requestDTO);
    RegisterResponseDto register(RegisterRequestDto requestDTO);
}
