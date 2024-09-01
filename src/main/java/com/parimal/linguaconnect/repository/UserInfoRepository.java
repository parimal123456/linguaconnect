package com.parimal.linguaconnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parimal.linguaconnect.entity.UserInfo;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo,Long>{
    Optional<UserInfo> findByEmail(String email);
    

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
