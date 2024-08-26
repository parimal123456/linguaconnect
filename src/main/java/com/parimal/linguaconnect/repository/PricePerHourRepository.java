package com.parimal.linguaconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.parimal.linguaconnect.entity.Language;
import com.parimal.linguaconnect.entity.Level;
import com.parimal.linguaconnect.entity.Teacher;
import com.parimal.linguaconnect.entity.PricePerHour;

public interface PricePerHourRepository extends JpaRepository<PricePerHour, Long> {
    Optional<PricePerHour> findByTeacherAndLanguageAndLevel(Teacher teacher, Language language, Level level);
}

