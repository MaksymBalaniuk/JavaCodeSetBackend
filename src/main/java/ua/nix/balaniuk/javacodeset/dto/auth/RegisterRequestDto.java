package ua.nix.balaniuk.javacodeset.dto.auth;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String username;
    private String password;
    private String email;
}
