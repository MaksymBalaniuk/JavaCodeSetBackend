package com.javacodeset.service.impl;

import com.javacodeset.entity.AuthorityEntity;
import com.javacodeset.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javacodeset.dto.permission.UserPermissionsDto;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.service.api.AuthorityService;
import com.javacodeset.util.AuthorityUtils;
import com.javacodeset.util.UserPermissionsPolicy;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImplementation implements AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

    @Override
    public Boolean isUserHasAdminAuthority(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id '%s' does not exist", userId)));
        return AuthorityUtils.mapToStringList(user.getAuthorities()).contains("ROLE_ADMIN");
    }

    @Override
    @Transactional
    public UserPermissionsDto getUserPermissions(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id '%s' does not exist", userId)));
        List<String> authorities = AuthorityUtils.mapToStringList(user.getAuthorities());
        return UserPermissionsPolicy.getUserPermissions(authorities);
    }

    @Override
    @Transactional
    public void addAdminAuthorityToUser(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id '%s' does not exist", userId)));
        AuthorityEntity adminAuthority = authorityRepository.findByName("ROLE_ADMIN").orElseThrow(() ->
                new NotFoundException("Admin authority not found"));

        user.getAuthorities().add(adminAuthority);
        adminAuthority.getUsers().add(user);
        userRepository.save(user);
        authorityRepository.save(adminAuthority);
    }
}
