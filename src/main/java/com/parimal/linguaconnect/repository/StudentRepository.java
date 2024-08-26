package com.parimal.linguaconnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parimal.linguaconnect.entity.Student;


public interface StudentRepository extends JpaRepository<Student,Long>{

    Optional<Student> findByUserInfoId(long id);
    
}
