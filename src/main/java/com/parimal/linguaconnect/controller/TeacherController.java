package com.parimal.linguaconnect.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parimal.linguaconnect.Dto.CourseDto;
import com.parimal.linguaconnect.Dto.TeacherLanguageDto;
import com.parimal.linguaconnect.Service.UserInfoService;
import com.parimal.linguaconnect.entity.Language;
import com.parimal.linguaconnect.entity.Level;
import com.parimal.linguaconnect.entity.SessionLength;
import com.parimal.linguaconnect.entity.Slot;
import com.parimal.linguaconnect.entity.Teacher;
import com.parimal.linguaconnect.entity.TeacherLanguage;
import com.parimal.linguaconnect.entity.TeacherLanguageCourse;
import com.parimal.linguaconnect.entity.UserInfo;
import com.parimal.linguaconnect.repository.LanguageRepository;
import com.parimal.linguaconnect.repository.LevelRepository;
import com.parimal.linguaconnect.repository.SessionLengthRepository;
import com.parimal.linguaconnect.repository.SlotRepository;
import com.parimal.linguaconnect.repository.TeacherLanguageCourseRepository;
import com.parimal.linguaconnect.repository.TeacherLanguageRepository;
import com.parimal.linguaconnect.repository.TeacherRepository;
import java.util.*;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/teacher/")
public class TeacherController {

    private final TeacherLanguageRepository teacherLanguageRepository;
    private final TeacherRepository teacherRepository;
    private final LanguageRepository languageRepository;
    private final UserInfoService userInfoService;
    private final LevelRepository levelRepository;
    private final SessionLengthRepository sessionLengthRepository;
    private final SlotRepository slotRepository;
    private final TeacherLanguageCourseRepository teacherLanguageCourseRepository;
    
    public ResponseEntity<?> pricePerHour(TeacherLanguageDto teacherLanguageDto,Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserInfo userInfo = userInfoService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Teacher teacher=teacherRepository.findByUserInfoId(userInfo.getId()).orElseThrow(()->new RuntimeException("Level Not Found"));
        Language language=languageRepository.findByLanguage(teacherLanguageDto.getLanguage()).orElseThrow(()->new RuntimeException("Language Not Found"));
        Level level=levelRepository.findByLevel(teacherLanguageDto.getLevel()).orElseThrow(()->new RuntimeException("Level Not Found"));
    
        TeacherLanguage teacherLanguage=teacherLanguageRepository.findByTeacherAndLanguageAndLevel(teacher, language, level).orElse(null);
        if(teacherLanguage==null){
            teacherLanguage=TeacherLanguage.builder()
                                                .teacher(teacher)
                                                .language(language)
                                                .level(level)
                                                .build();
        }
        teacherLanguage.setPricePerHour(Double.valueOf(teacherLanguageDto.getPricePerHour()));
        teacher.getTeacherLanguages().add(teacherLanguage);
        language.getTeacherLanguages().add(teacherLanguage);
           
        teacherLanguageRepository.save(teacherLanguage);
        teacherRepository.save(teacher);
        languageRepository.save(language);
        teacher.getTeacherLanguages().forEach(l->System.out.println(l.getLanguage().getLanguage()));
        return new ResponseEntity<>(teacherLanguageDto,HttpStatus.OK);
    }

    @PostMapping("priceperhourall")
    public ResponseEntity<?> pricePerHourAll(@RequestBody List<TeacherLanguageDto> teacherLanguageDtos, Authentication authentication) {

    teacherLanguageDtos.forEach(dto -> pricePerHour(dto, authentication));
    
    return new ResponseEntity<>(teacherLanguageDtos, HttpStatus.OK);
}


    @PostMapping("course")
    public ResponseEntity<?> createcourse(@RequestBody CourseDto courseDto,Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserInfo userInfo = userInfoService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
                Teacher teacher=teacherRepository.findByUserInfoId(userInfo.getId()).orElseThrow(()->new RuntimeException("Level Not Found"));
                Language language=languageRepository.findByLanguage(courseDto.getLanguage()).orElseThrow(()->new RuntimeException("Language Not Found"));
                Level level=levelRepository.findByLevel(courseDto.getLevel()).orElseThrow(()->new RuntimeException("Level Not Found"));
               
                List<SessionLength> sessionLengths=courseDto.getSessionLengths().stream()
                                                                                .map(len->{
                                                                                    SessionLength sessionLength=sessionLengthRepository.findByLength(Integer.valueOf(len)).orElse(null);
                                                                                    if(sessionLength==null){
                                                                                        sessionLength=SessionLength.builder()
                                                                                                                    .length(Integer.valueOf(len))
                                                                                                                    .build();
                                                                                        sessionLengthRepository.save(sessionLength);
                                                                                    }
                                                                                    return sessionLength;
                                                                                })
                                                                                .collect(Collectors.toList());
                
                List<Slot> slots = courseDto.getSlots().entrySet().stream()
                                                                    .map(entry -> {
                                                                        String s = entry.getKey();
                                                                        String d = entry.getValue();
                                                                        
                                                                        Slot slot = slotRepository.findBySlot(s).orElse(null);
                                                                        if (slot == null) {
                                                                            slot = Slot.builder()
                                                                                    .slot(s)
                                                                                    .description(d)
                                                                                    .build();
                                                                            slotRepository.save(slot);
                                                                        }
                                                                        slot.setDescription(d);
                                                                        return slot;
                                                                    })
                                                                    .collect(Collectors.toList());

    TeacherLanguage teacherLanguage=teacherLanguageRepository.findByTeacherAndLanguageAndLevel(teacher, language, level).orElse(null);
    if(teacherLanguage==null){
        return new ResponseEntity<>("cannot create course",HttpStatus.BAD_REQUEST);
    }
    
    slots.forEach(slot->{
        sessionLengths.forEach(len->{
            TeacherLanguageCourse teacherLanguageCourse=TeacherLanguageCourse.builder()
                                                                                .teacherLanguage(teacherLanguage)
                                                                                .sessionLength(len)
                                                                                .slot(slot)
                                                                                .build();
            teacherLanguage.getTeacherLanguageCourses().add(teacherLanguageCourse);                                                                    
            teacherLanguageCourseRepository.save(teacherLanguageCourse);
        });
    });
        teacherLanguageRepository.save(teacherLanguage);
        return new ResponseEntity<>(courseDto,HttpStatus.OK);
    }

}
