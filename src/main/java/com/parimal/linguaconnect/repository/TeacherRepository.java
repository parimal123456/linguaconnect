package com.parimal.linguaconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.parimal.linguaconnect.entity.Teacher;
@Repository
public interface TeacherRepository extends JpaRepository<Teacher,Long>{
    Optional<Teacher> findByUserInfoId(Long userInfoid);
}
