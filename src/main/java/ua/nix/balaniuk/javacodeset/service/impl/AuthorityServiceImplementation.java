package ua.nix.balaniuk.javacodeset.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.nix.balaniuk.javacodeset.dto.permission.UserPermissionsDto;
import ua.nix.balaniuk.javacodeset.entity.AuthorityEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.exception.NotFoundException;
import ua.nix.balaniuk.javacodeset.repository.AuthorityRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.service.api.AuthorityService;
import ua.nix.balaniuk.javacodeset.util.AuthorityUtils;
import ua.nix.balaniuk.javacodeset.util.UserPermissionsPolicy;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImplementation implements AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

    @Override
    public AuthorityEntity getByName(String authorityName) {
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
