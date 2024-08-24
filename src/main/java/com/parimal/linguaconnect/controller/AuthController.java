package com.parimal.linguaconnect.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.parimal.linguaconnect.Dto.LoginDto;
import com.parimal.linguaconnect.Dto.RegisterDto;
import com.parimal.linguaconnect.Service.AuthenticationService;
import com.parimal.linguaconnect.Service.JwtService;
import com.parimal.linguaconnect.Service.TokenService;
import com.parimal.linguaconnect.Service.UserInfoService;
import com.parimal.linguaconnect.entity.UserInfo;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserInfoService userInfoService;
    private final TokenService tokenService;
    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {

        if (userInfoService.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }
        if (userInfoService.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("Email is already registered", HttpStatus.BAD_REQUEST);
        }

        UserInfo userInfo=authenticationService.save(registerDto);

        UserDetails userDetails = userInfoService.loadUserByUsername(userInfo.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);

        tokenService.save(userInfo,jwtToken);
        return new ResponseEntity<>(jwtToken, HttpStatus.OK);
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

        UserDetails userDetails = userInfoService.loadUserByUsername(loginDto.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);

        UserInfo userInfo=userInfoService.findByUsername(loginDto.getUsername()).orElseThrow();
        tokenService.revokeAllUserTokens(userInfo);
        tokenService.save(userInfo,jwtToken);

        return new ResponseEntity<>(jwtToken, HttpStatus.OK);
    }

    @GetMapping("logout/message")
    public ResponseEntity<String> logot(){
        return new ResponseEntity<>("successfully logged out",HttpStatus.OK);
    }
}