package com.parimal.linguaconnect.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyUserDto {
    private String email;
    private String verificationCode;
    
}
