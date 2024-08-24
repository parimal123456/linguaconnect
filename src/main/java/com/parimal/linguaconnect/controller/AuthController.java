package com.parimal.linguaconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Security;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parimal.linguaconnect.Dto.LoginDto;
import com.parimal.linguaconnect.Dto.RegisterDto;
import com.parimal.linguaconnect.Service.JwtService;
import com.parimal.linguaconnect.Service.UserInfoService;
import com.parimal.linguaconnect.customexceptions.RoleNotFoundException;
import com.parimal.linguaconnect.entity.Role;
import com.parimal.linguaconnect.entity.UserInfo;
import com.parimal.linguaconnect.repository.RoleRepository;
import com.parimal.linguaconnect.repository.UserInfoRepository;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserInfoService userInfoService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, 
                          UserInfoRepository userInfoRepository, 
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService, 
                          UserInfoService userInfoService) {
        this.authenticationManager = authenticationManager;
        this.userInfoRepository = userInfoRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userInfoService = userInfoService;
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (userInfoRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }
        if (userInfoRepository.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("Email is already registered", HttpStatus.BAD_REQUEST);
        }

        UserInfo user = new UserInfo();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        Role roles = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RoleNotFoundException("Role not found"));
        user.setRoles(Collections.singletonList(roles));

        userInfoRepository.save(user);

        UserDetails userDetails = userInfoService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        Authentication authentication;
        try{
            authentication= authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        }
        catch(AuthenticationException e){
            return new ResponseEntity<>("Not Authenticated User- "+e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails;
        try{
             userDetails = userInfoService.loadUserByUsername(loginDto.getUsername());
        }
        catch(UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        String token = jwtService.generateToken(userDetails);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}