package com.parimal.linguaconnect.Service;


import java.util.Optional;

import org.springframework.stereotype.Service;

import com.parimal.linguaconnect.entity.TempUser;
import com.parimal.linguaconnect.repository.TempUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TempUserService {

    private final TempUserRepository tempUserRepository;

    public TempUser saveTempUser(TempUser tempUser) {
        return tempUserRepository.save(tempUser);
    }
    public Optional<TempUser> findTempByEmail(String email) {
        return tempUserRepository.findByTempEmail(email);
    }
    public boolean existsByTempEmail(String email) {
        return tempUserRepository.existsByTempEmail(email);
    }
    public Optional<TempUser> findByVerificationCode(String verificationCode) {
        return tempUserRepository.findByVerificationCode(verificationCode);
    }
    public void deleteTempUser(TempUser tempUser) {
        tempUserRepository.delete(tempUser);
    }
            
    
    

}
