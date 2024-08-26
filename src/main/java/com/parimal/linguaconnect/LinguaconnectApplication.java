package com.parimal.linguaconnect;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.parimal.linguaconnect.entity.Language;
import com.parimal.linguaconnect.entity.Level;
import com.parimal.linguaconnect.entity.Role;
import com.parimal.linguaconnect.entity.SessionLength;
import com.parimal.linguaconnect.entity.Slot;
import com.parimal.linguaconnect.repository.LanguageRepository;
import com.parimal.linguaconnect.repository.LevelRepository;
import com.parimal.linguaconnect.repository.RoleRepository;
import com.parimal.linguaconnect.repository.SessionLengthRepository;
import com.parimal.linguaconnect.repository.SlotRepository;

@SpringBootApplication
public class LinguaconnectApplication implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private SessionLengthRepository sessionLengthRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private SlotRepository slotRepository;

    public static void main(String[] args) {
        SpringApplication.run(LinguaconnectApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Creating roles
        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");

        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");

        Role roleTeacher =new Role();
        roleTeacher.setName("ROLE_TEACHER");

        Role roleStudent =new Role();
        roleStudent.setName("ROLE_STUDENT");

        roleRepository.saveAll(List.of(roleAdmin, roleUser, roleTeacher, roleStudent));

        // Creating languages
        Language language1 = new Language();
        language1.setLanguage("English");

        Language language2 = new Language();
        language2.setLanguage("French");

        Language language3 = new Language();
        language3.setLanguage("Spanish");

        languageRepository.saveAll(List.of(language1, language2, language3));

        SessionLength length1=new SessionLength();
        length1.setLength(45);
        SessionLength length2=new SessionLength();
        length2.setLength(60);
        SessionLength length3=new SessionLength();
        length3.setLength(90);

        sessionLengthRepository.saveAll(List.of(length1,length2,length3));

        Level l1=new Level();
        l1.setLevel("Beginner");
        Level l2=new Level();
        l2.setLevel("Intermediate");
        Level l3=new Level();
        l3.setLevel("Advanced");

        levelRepository.saveAll(List.of(l1,l2,l3));

        Slot s1=new Slot();
        s1.setSlot("Morning");
        s1.setDescription("Morning");
        Slot s2=new Slot();
        s2.setSlot("Afternoon");
        s2.setDescription("Afternoon");
        Slot s3=new Slot();
        s3.setSlot("Evening");
        s3.setDescription("Evening");
        
        slotRepository.saveAll(List.of(s1,s2,s3));
    }
}
