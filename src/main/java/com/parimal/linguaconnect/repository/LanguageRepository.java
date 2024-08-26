package com.parimal.linguaconnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parimal.linguaconnect.entity.Language;

@Repository
public interface LanguageRepository extends JpaRepository<Language,Long>{
    Optional<Language> findByLanguage(String language);
}
