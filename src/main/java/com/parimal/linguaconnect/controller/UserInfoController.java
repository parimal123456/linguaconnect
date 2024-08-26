package com.parimal.linguaconnect.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parimal.linguaconnect.Dto.TeacherDto;
import com.parimal.linguaconnect.Dto.TeacherEnrollDto;
import com.parimal.linguaconnect.Service.UserInfoService;
import com.parimal.linguaconnect.entity.Language;
import com.parimal.linguaconnect.entity.Role;
import com.parimal.linguaconnect.entity.Student;
import com.parimal.linguaconnect.entity.Teacher;
import com.parimal.linguaconnect.entity.UserInfo;
import com.parimal.linguaconnect.repository.LanguageRepository;
import com.parimal.linguaconnect.repository.RoleRepository;
import com.parimal.linguaconnect.repository.StudentRepository;
import com.parimal.linguaconnect.repository.TeacherRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user/")
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final RoleRepository roleRepository;
    private final LanguageRepository languageRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @PostMapping("enroll/teacher")
    public ResponseEntity<?> teacherEnroll(@RequestBody TeacherEnrollDto teacherEnrollDto, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Fetch the user info based on the authenticated user's email
        UserInfo userInfo = userInfoService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(userInfo.getRoles().contains(roleRepository.findByName("ROLE_TEACHER")
                .orElseThrow(() -> new RuntimeException("Role not found")))){
                return new ResponseEntity<>("already enrolled as teacher",HttpStatus.BAD_REQUEST);
        }

        // Fetch the languages based on the provided names in the DTO
        List<Language> languages = teacherEnrollDto.getLanguages()
                .stream()
                .map(lan -> languageRepository.findByLanguage(lan)
                        .orElseThrow(() -> new RuntimeException("Language not found: " + lan)))
                .collect(Collectors.toList());

        // Build the teacher entity
        Teacher teacher =Teacher.builder()
                                .experience(teacherEnrollDto.getExperience())
                                .userInfo(userInfo)
                                .languages(languages)
                                .build();

        // Save the teacher entity
        teacherRepository.save(teacher);
        List<Role> roles = userInfo.getRoles();
        roles.add(roleRepository.findByName("ROLE_TEACHER")
        .orElseThrow(() -> new RuntimeException("Role not found")));

        userInfoService.save(userInfo);

        // List<Level> levels=levelRepository.findAll();
        // languages.forEach(language->{
        //         levels.forEach(level->{
        //                 TeacherLanguage teacherLanguage =TeacherLanguage.builder()
        //                                                                 .teacher(teacher)
        //                                                                 .level(level)
        //                                                                 .language(language)
        //                                                                 .pricePerHour(-1)
        //                                                                 .build();
        //                 teacherLanguageRepository.save(teacherLanguage);
        //         });            
        // });
                                                    
        TeacherDto teacherDto =TeacherDto.builder()
                                        .username(userInfo.getUsername())
                                        .experience(teacher.getExperience())
                                        .languages(teacherEnrollDto.getLanguages())
                                        .build();

        return new ResponseEntity<>(teacherDto, HttpStatus.OK);
    }

    @PostMapping("enroll/student")
    public ResponseEntity<?> studentEnroll(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Fetch the user info based on the authenticated user's email
        UserInfo userInfo = userInfoService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(userInfo.getRoles().contains(roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new RuntimeException("Role not found")))){
                return new ResponseEntity<>("already enrolled as student",HttpStatus.BAD_REQUEST);
        }
        Student student= Student.builder()
                                .userInfo(userInfo)
                                .teacherLanguageCourses(List.of())
                                .build();

        studentRepository.save(student);
        List<Role> roles = userInfo.getRoles();
        roles.add(roleRepository.findByName("ROLE_STUDENT")
        .orElseThrow(() -> new RuntimeException("Role not found")));

        userInfoService.save(userInfo);

        return new ResponseEntity<>("student enrollement success",HttpStatus.OK);
        }
}
