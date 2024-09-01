package com.parimal.linguaconnect.controller;


import java.net.URI;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parimal.linguaconnect.Dto.LoginDto;
import com.parimal.linguaconnect.Dto.RegisterDto;
import com.parimal.linguaconnect.Dto.TempDto;
import com.parimal.linguaconnect.Dto.VerifyUserDto;
import com.parimal.linguaconnect.Service.AuthenticationService;
import com.parimal.linguaconnect.Service.AuthenticationService.VerificationCodeAlreadySentException;
import com.parimal.linguaconnect.Service.JwtService;
import com.parimal.linguaconnect.Service.TokenService;
import com.parimal.linguaconnect.Service.UserInfoService;
import com.parimal.linguaconnect.entity.TempUser;
import com.parimal.linguaconnect.entity.UserInfo;
import com.parimal.linguaconnect.repository.TempUserRepository;
import com.parimal.linguaconnect.repository.UserInfoRepository;

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
    private final UserInfoRepository userInfoRepository;
    private final TempUserRepository tempUserRepository;

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {

        if (userInfoService.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("Email is already taken", HttpStatus.BAD_REQUEST);
        }
        if (userInfoService.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }
        

       try{ authenticationService.save(registerDto);}
       catch(VerificationCodeAlreadySentException e ){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
       }
       

        // UserDetails userDetails = userInfoService.loadUserByUsername(userInfo.getEmail());
        //String jwtToken = jwtService.generateToken(userDetails);

        //tokenService.save(userInfo,jwtToken);
        //return new ResponseEntity<>(jwtToken, HttpStatus.OK);

         HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create("/verify"));
    return new ResponseEntity<>("Registration in process. Please check your email for verification.", HttpStatus.ACCEPTED);
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {

        Authentication authentication;
        try{
            authentication= authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        }
        catch(AuthenticationException e){
            return new ResponseEntity<>("Not Authenticated User- "+e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userInfoService.loadUserByUsername(loginDto.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        UserInfo userInfo=userInfoService.findByEmail(loginDto.getEmail()).orElseThrow();
        tokenService.revokeAllUserTokens(userInfo);
        tokenService.save(userInfo,jwtToken);

        return new ResponseEntity<>(jwtToken, HttpStatus.OK);
    }

    @PostMapping("verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {

       
      //  UserInfo userInfo=userInfoService.findByEmail(userDetails.getUsername()).orElseThrow(()->new UsernameNotFoundException("user not found"));
       Optional<UserInfo> optionalUser = userInfoRepository.findByEmail(verifyUserDto.getEmail());
       if (optionalUser.isPresent()){
        throw new RuntimeException("Already registered");
       }

       Optional<TempUser> optionalTempUser = tempUserRepository.findByTempEmail(verifyUserDto.getEmail());
        if (!optionalTempUser.isPresent()) {
            throw new RuntimeException("Register again");
        }
        TempUser tempUser = optionalTempUser.get();
        
        try {
            authenticationService.verifyUser(verifyUserDto);
            UserDetails userDetails = userInfoService.loadUserByUsername(verifyUserDto.getEmail());
            String jwtToken = jwtService.generateToken(userDetails);

            UserInfo userInfo= userInfoService.findByEmail(userDetails.getUsername()).orElseThrow();

            //tokenService.save(userInfo,jwtToken);
            tokenService.save(userInfo,jwtToken);
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @PostMapping("resend")
    public ResponseEntity<?> resendVerificationCode(@RequestBody TempDto tempDto) {
        try {
            authenticationService.resendVerificationCode(tempDto.getEmail());
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}