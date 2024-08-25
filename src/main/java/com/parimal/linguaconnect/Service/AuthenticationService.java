package com.parimal.linguaconnect.Service;

import java.util.Collections;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.parimal.linguaconnect.Dto.RegisterDto;
import com.parimal.linguaconnect.entity.Role;
import com.parimal.linguaconnect.entity.UserInfo;
import com.parimal.linguaconnect.repository.RoleRepository;
import com.parimal.linguaconnect.repository.UserInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;                                       
    private final UserInfoRepository userInfoRepository;

    public UserInfo save(RegisterDto registerDto){

        Role roles = roleRepository.findByName("ROLE_USER").orElseThrow();

        UserInfo userInfo = UserInfo.builder()
        .username(registerDto.getUsername())
        .email(registerDto.getEmail())
        .password(passwordEncoder.encode(registerDto.getPassword()))
        .roles(Collections.singletonList(roles))
        .build();

        return userInfoRepository.save(userInfo);
    }
}
