package ua.nix.balaniuk.javacodeset.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.nix.balaniuk.javacodeset.dto.UserDto;
import ua.nix.balaniuk.javacodeset.dto.premium.PremiumLimitsDto;
import ua.nix.balaniuk.javacodeset.entity.AuthorityEntity;
import ua.nix.balaniuk.javacodeset.entity.UserEntity;
import ua.nix.balaniuk.javacodeset.enumeration.UserPremium;
import ua.nix.balaniuk.javacodeset.enumeration.UserStatus;
import ua.nix.balaniuk.javacodeset.exception.BadRequestException;
import ua.nix.balaniuk.javacodeset.exception.NotFoundException;
import ua.nix.balaniuk.javacodeset.exception.ProhibitedOperationException;
import ua.nix.balaniuk.javacodeset.repository.AuthorityRepository;
import ua.nix.balaniuk.javacodeset.repository.UserRepository;
import ua.nix.balaniuk.javacodeset.service.api.UserService;
import ua.nix.balaniuk.javacodeset.util.PremiumLimitsPolicy;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserEntity create(UserDto userDto) {
        validateCreationUserDto(userDto);

        AuthorityEntity basicAuthority = authorityRepository.findByName("ROLE_USER").orElseThrow(() ->
                new NotFoundException("Base authority not found"));

        UserEntity user = modelMapper.map(userDto, UserEntity.class);
        user.setId(null);
        user.setStatus(UserStatus.ACTIVE);
        user.setPremium(UserPremium.NONE);
        user.getAuthorities().add(basicAuthority);
        user = userRepository.save(user);
        basicAuthority.getUsers().add(user);
        authorityRepository.save(basicAuthority);
        return user;
    }

    private void validateCreationUserDto(UserDto userDto) {
        if (Objects.equals(userDto.getUsername().trim(), ""))
            throw new BadRequestException("Username cannot be empty");

        if (Objects.equals(userDto.getPassword().trim(), ""))
            throw new BadRequestException("Password cannot be empty");

        if (Objects.equals(userDto.getEmail().trim(), ""))
            throw new BadRequestException("Email cannot be empty");

        if (userRepository.existsByUsername(userDto.getUsername()))
            throw new BadRequestException(
                    String.format("User with username '%s' already exist", userDto.getUsername()));

        if (userRepository.existsByEmail(userDto.getEmail()))
            throw new BadRequestException(
                    String.format("User with email '%s' already exist", userDto.getEmail()));
    }

    @Override
    public UserEntity get(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id '%s' does not exist", userId)));
    }

    @Override
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity update(UserDto userDto) {
        throw new ProhibitedOperationException("Update user operation is prohibited for security reasons");
    }

    @Override
    public void delete(UUID userId) {
        throw new ProhibitedOperationException("Delete user operation is prohibited, use 'markDeleted'");
    }

    @Override
    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new NotFoundException(String.format("User with username '%s' does not exist", username)));
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserEntity updateUsername(UUID userId, String username) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id '%s' does not exist", userId)));

        if (userRepository.existsByUsername(username))
            throw new BadRequestException(String.format("User with username '%s' already exist", username));

        user.setUsername(username);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserEntity activateUserById(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id '%s' does not exist", userId)));

        if (Objects.equals(user.getStatus(), UserStatus.DELETED))
            throw new BadRequestException(String.format("User with id '%s' deleted", user.getId()));

        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserEntity banUserById(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id '%s' does not exist", userId)));

        if (Objects.equals(user.getStatus(), UserStatus.DELETED))
            throw new BadRequestException(String.format("User with id '%s' deleted", user.getId()));

        user.setStatus(UserStatus.BANNED);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserEntity markUserDeletedById(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id '%s' does not exist", userId)));
        user.setStatus(UserStatus.DELETED);
        return userRepository.save(user);
    }

    @Override
    public PremiumLimitsDto getUserPremiumLimits(UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id '%s' does not exist", userId)));
        return PremiumLimitsPolicy.getPremiumLimits(user.getPremium());
    }
}
