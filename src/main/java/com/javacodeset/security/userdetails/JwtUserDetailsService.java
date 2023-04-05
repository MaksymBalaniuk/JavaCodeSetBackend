package com.javacodeset.security.userdetails;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.javacodeset.dto.UserDto;
import com.javacodeset.entity.UserEntity;
import com.javacodeset.repository.UserRepository;
import com.javacodeset.util.AuthorityUtils;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

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
}
