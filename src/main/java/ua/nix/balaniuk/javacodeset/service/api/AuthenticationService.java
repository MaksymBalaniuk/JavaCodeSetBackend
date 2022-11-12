package ua.nix.balaniuk.javacodeset.service.api;

import ua.nix.balaniuk.javacodeset.dto.auth.AuthenticationRequestDto;
import ua.nix.balaniuk.javacodeset.dto.auth.AuthenticationResponseDto;
import ua.nix.balaniuk.javacodeset.dto.auth.RegisterRequestDto;
import ua.nix.balaniuk.javacodeset.dto.auth.RegisterResponseDto;

public interface AuthenticationService {
    AuthenticationResponseDto login(AuthenticationRequestDto requestDTO);
    RegisterResponseDto register(RegisterRequestDto requestDTO);
}
