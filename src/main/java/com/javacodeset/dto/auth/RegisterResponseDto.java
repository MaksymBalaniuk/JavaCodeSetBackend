package com.javacodeset.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDto {
    private Boolean existsByUsername;
    private Boolean existsByEmail;
    private String token;
    private UUID id;
}
