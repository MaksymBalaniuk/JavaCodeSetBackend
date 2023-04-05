package com.javacodeset.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javacodeset.dto.permission.UserPermissionsDto;
import com.javacodeset.entity.AuthorityEntity;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.exception.NotFoundException;
import com.javacodeset.repository.AuthorityRepository;
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
    public AuthorityEntity getAuthorityByName(String authorityName) {
        return authorityRepository.findByName(authorityName).orElseThrow(() ->
                new NotFoundException(String.format("Authority with name '%s' does not exist", authorityName)));
    }

    @Override
    @Transactional
    public UserPermissionsDto getUserPermissions(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id '%s' does not exist", userId)));
        List<String> authorities = AuthorityUtils.mapToStringList(user.getAuthorities());
        return UserPermissionsPolicy.getUserPermissions(authorities);
    }
}
