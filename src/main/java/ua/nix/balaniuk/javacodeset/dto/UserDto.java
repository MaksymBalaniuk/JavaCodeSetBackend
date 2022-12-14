package ua.nix.balaniuk.javacodeset.dto;

import lombok.Data;
import ua.nix.balaniuk.javacodeset.enumeration.UserPremium;
import ua.nix.balaniuk.javacodeset.enumeration.UserStatus;

import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private UserStatus status;
    private UserPremium premium;
}
