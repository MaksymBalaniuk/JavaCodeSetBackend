package com.javacodeset.dto;

import lombok.Data;
import com.javacodeset.enumeration.UserPremium;
import com.javacodeset.enumeration.UserStatus;

import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private UserStatus status;
    private UserPremium premium;
    private Long created;
    private Long updated;
}
