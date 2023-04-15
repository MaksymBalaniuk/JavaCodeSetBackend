package com.javacodeset.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/get/{userId}/is-admin")
    public ResponseEntity<Boolean> isUserHasAdminAuthority(@PathVariable UUID userId) {
        return new ResponseEntity<>(authorityService.isUserHasAdminAuthority(userId), HttpStatus.OK);
    }

    @PostMapping("/add/authority-to-user/admin/{userId}")
    public ResponseEntity<Object> addAdminAuthorityToUser(@PathVariable UUID userId) {
        authorityService.addAdminAuthorityToUser(userId);
        return ResponseEntity.noContent().build();
    }
}
