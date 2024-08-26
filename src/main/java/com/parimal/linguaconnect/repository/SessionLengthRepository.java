package com.parimal.linguaconnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parimal.linguaconnect.entity.SessionLength;
@Repository
public interface SessionLengthRepository extends JpaRepository<SessionLength,Long> {

    Optional<SessionLength> findByLength(Integer length);

}
