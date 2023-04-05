package com.javacodeset.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.javacodeset.dto.permission.UserPermissionsDto;
import com.javacodeset.service.api.AuthorityService;

import java.util.UUID;

@RestController
@RequestMapping("/api/authorities")
@RequiredArgsConstructor
public class AuthorityRestController {

    private final AuthorityService authorityService;

    @GetMapping("/get/{userId}/permissions")
    public UserPermissionsDto getUserPermissions(@PathVariable UUID userId) {
        return authorityService.getUserPermissions(userId);
    }
}
