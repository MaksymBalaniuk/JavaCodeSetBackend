package com.javacodeset.security.userdetails;

import com.javacodeset.exception.BadRequestException;
import com.javacodeset.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javacodeset.dto.UserDto;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.util.AuthorityUtils;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsServiceImplementation implements JwtUserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(username));
        return JwtUserFactory.create(modelMapper.map(user, UserDto.class),
                AuthorityUtils.mapToGrantedAuthorities(user.getAuthorities()));
    }

    @Override
    public UserEntity getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() ->
                new NotFoundException(String.format("User with username '%s' does not exist", username)));
    }

    @Override
    @Transactional
    public UserEntity updateAuthenticatedUserUsername(String username) {
        UserEntity user = getAuthenticatedUser();

        if (userRepository.existsByUsername(username))
            throw new BadRequestException(String.format("User with username '%s' already exist", username));

        user.setUsername(username);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserEntity updateAuthenticatedUserEmail(String email) {
        UserEntity user = getAuthenticatedUser();

        if (userRepository.existsByEmail(email))
            throw new BadRequestException(String.format("User with email '%s' already exist", email));

        user.setEmail(email);
        return userRepository.save(user);
    }
}
