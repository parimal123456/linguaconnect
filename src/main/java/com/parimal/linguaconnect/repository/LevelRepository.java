package com.parimal.linguaconnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parimal.linguaconnect.entity.Level;

@Repository
public interface LevelRepository extends JpaRepository<Level,Long> {

    Optional<Level> findByLevel(String level);

}
