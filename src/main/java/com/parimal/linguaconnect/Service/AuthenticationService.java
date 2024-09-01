package com.parimal.linguaconnect.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.parimal.linguaconnect.Dto.RegisterDto;
import com.parimal.linguaconnect.Dto.VerifyUserDto;
import com.parimal.linguaconnect.entity.Role;
import com.parimal.linguaconnect.entity.TempUser;
import com.parimal.linguaconnect.entity.UserInfo;
import com.parimal.linguaconnect.repository.RoleRepository;
import com.parimal.linguaconnect.repository.TempUserRepository;
import com.parimal.linguaconnect.repository.UserInfoRepository;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;                                       
    private final UserInfoRepository userInfoRepository;
    private final EmailService emailService;
    // private final TempUser tempUser;
    private final TempUserRepository tempUserRepository;
    private final TempUserService tempUserService;


    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public TempUser save(RegisterDto registerDto){

     TempUser tempUser = tempUserRepository.findByTempEmail(registerDto.getEmail()).orElse(null);
        if ((tempUser!=null) && (tempUser.getVerificationCodeExpiresAt().isAfter(LocalDateTime.now()))){
          
                    //throw new RuntimeException("Verification code has been sent");
                    throw new VerificationCodeAlreadySentException("Verification code has already been sent. Please check your email.");
                }else if (tempUser!=null){

                    tempUser.setTempUsername(registerDto.getUsername());
                    tempUser.setTempPassword(passwordEncoder.encode(registerDto.getPassword()) );
                    tempUser.setVerificationCode(generateVerificationCode());
                    tempUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
                    sendVerificationEmail(tempUser);
                    return tempUserRepository.save(tempUser);
                }
                else{
                    TempUser finUser= new TempUser();
                    finUser.setTempEmail(registerDto.getEmail());
                    finUser.setTempUsername(registerDto.getUsername());
                    finUser.setTempPassword(passwordEncoder.encode(registerDto.getPassword()) );
                    finUser.setVerificationCode(generateVerificationCode());
                    finUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
                    sendVerificationEmail(finUser);
                    return tempUserRepository.save(finUser);

                }
    }




    public void verifyUser(VerifyUserDto input) {


        Optional<TempUser> optionalUser = tempUserRepository.findByTempEmail(input.getEmail());
        if (optionalUser.isPresent()) {
                TempUser tempUser = optionalUser.get();
                if (tempUser.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                    throw new RuntimeException("Verification code has expired");
                }
                if (tempUser.getVerificationCode().equals(input.getVerificationCode())) {
                    tempUser.setVerificationCode(input.getVerificationCode());
                    tempUser.setVerificationCodeExpiresAt(null);
                    tempUserRepository.save(tempUser);

                    Role roles = roleRepository.findByName("ROLE_USER").orElseThrow();

                     UserInfo userInfo = UserInfo.builder()
                .username(tempUser.getTempUsername())
                .email(tempUser.getTempEmail())
                .password(tempUser.getTempPassword()) // Ensure password is encrypted
                .roles(Collections.singletonList(roles))
                .build();

                userInfoRepository.save(userInfo);
                tempUserService.deleteTempUser(tempUser);
                } else {
                    throw new RuntimeException("Invalid verification code");
                }
            } else {
                    throw new RuntimeException("User not found");
                }

    }

    
    public void resendVerificationCode(String email) {
        
        if(userInfoRepository.existsByEmail(email)){
            throw new RuntimeException("Account is already verified");
        }
       else if (tempUserRepository.existsByTempEmail(email)) {

            TempUser tempUser = tempUserRepository.findByTempEmail(email).orElseThrow(()->new RuntimeException("Register again"));

        // Generate a new verification code and update the expiration time
        tempUser.setVerificationCode(generateVerificationCode());
        tempUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));

        tempUserRepository.save(tempUser);
        // Send the verification email with the new code
        sendVerificationEmail(tempUser);
        
        }
        else {
            throw new RuntimeException("User not found");
        }
    }



    private void sendVerificationEmail(TempUser tempUser) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + tempUser.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(tempUser.getTempEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            logger.error("Error sending verification email to {}: {}", tempUser.getTempEmail(), e.getMessage(), e);
        }
    }


    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) // Respond with 400 Bad Request
public class VerificationCodeAlreadySentException extends RuntimeException {
    public VerificationCodeAlreadySentException(String message) {
        super(message);
    }
}
}
