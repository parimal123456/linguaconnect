package com.parimal.linguaconnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parimal.linguaconnect.entity.TempUser;

public interface TempUserRepository extends JpaRepository<TempUser, Long>{
     Optional<TempUser> findByTempEmail(String tempEmail);
    Optional<TempUser> findByVerificationCode(String verificationCode);

    Boolean existsByTempUsername(String tempUsername);
    Boolean existsByTempEmail(String tempEmail);

}
