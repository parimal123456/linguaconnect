package com.parimal.linguaconnect.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parimal.linguaconnect.Dto.CourseDto;
import com.parimal.linguaconnect.Dto.PricePerHourDto;
import com.parimal.linguaconnect.Service.UserInfoService;
import com.parimal.linguaconnect.entity.Language;
import com.parimal.linguaconnect.entity.Level;
import com.parimal.linguaconnect.entity.SessionLength;
import com.parimal.linguaconnect.entity.Slot;
import com.parimal.linguaconnect.entity.Teacher;
import com.parimal.linguaconnect.entity.PricePerHour;
import com.parimal.linguaconnect.entity.Course;
import com.parimal.linguaconnect.entity.UserInfo;
import com.parimal.linguaconnect.repository.LanguageRepository;
import com.parimal.linguaconnect.repository.LevelRepository;
import com.parimal.linguaconnect.repository.SessionLengthRepository;
import com.parimal.linguaconnect.repository.SlotRepository;
import com.parimal.linguaconnect.repository.CourseRepository;
import com.parimal.linguaconnect.repository.PricePerHourRepository;
import com.parimal.linguaconnect.repository.TeacherRepository;
import java.util.*;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/teacher/")
public class TeacherController {

    private final PricePerHourRepository pricePerHourRepository;
    private final TeacherRepository teacherRepository;
    private final LanguageRepository languageRepository;
    private final UserInfoService userInfoService;
    private final LevelRepository levelRepository;
    private final SessionLengthRepository sessionLengthRepository;
    private final SlotRepository slotRepository;
    private final CourseRepository courseRepository;
    
    public ResponseEntity<?> pricePerHour(PricePerHourDto pricePerHourDto,Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserInfo userInfo = userInfoService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Teacher teacher=teacherRepository.findByUserInfoId(userInfo.getId()).orElseThrow(()->new RuntimeException("Level Not Found"));
        Language language=languageRepository.findByLanguage(pricePerHourDto.getLanguage()).orElseThrow(()->new RuntimeException("Language Not Found"));
        Level level=levelRepository.findByLevel(pricePerHourDto.getLevel()).orElseThrow(()->new RuntimeException("Level Not Found"));
    
        PricePerHour pricePerHour=pricePerHourRepository.findByTeacherAndLanguageAndLevel(teacher, language, level).orElse(null);
        if(pricePerHour==null){
            pricePerHour=PricePerHour.builder()
                                                .teacher(teacher)
                                                .language(language)
                                                .level(level)
                                                .build();
        }
        pricePerHour.setPricePerHour(Double.valueOf(pricePerHourDto.getPricePerHour()));
        teacher.getPricePerHours().add(pricePerHour);
        language.getPricePerHours().add(pricePerHour);
           
        pricePerHourRepository.save(pricePerHour);
        teacherRepository.save(teacher);
        languageRepository.save(language);
        teacher.getPricePerHours().forEach(l->System.out.println(l.getLanguage().getLanguage()));
        return new ResponseEntity<>(pricePerHourDto,HttpStatus.OK);
    }

    @PostMapping("priceperhourall")
    public ResponseEntity<?> pricePerHourAll(@RequestBody List<PricePerHourDto> pricePerHourDtos, Authentication authentication) {

    pricePerHourDtos.forEach(dto -> pricePerHour(dto, authentication));
    
    return new ResponseEntity<>(pricePerHourDtos, HttpStatus.OK);
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

    PricePerHour pricePerHour=pricePerHourRepository.findByTeacherAndLanguageAndLevel(teacher, language, level).orElse(null);
    if(pricePerHour==null){
        return new ResponseEntity<>("cannot create course",HttpStatus.BAD_REQUEST);
    }
    
    slots.forEach(slot->{
        sessionLengths.forEach(len->{
            Course course=Course.builder()
                                                                                .pricePerHour(pricePerHour)
                                                                                .sessionLength(len)
                                                                                .slot(slot)
                                                                                .build();
            pricePerHour.getCourses().add(course);                                                                    
            courseRepository.save(course);
        });
    });
        pricePerHourRepository.save(pricePerHour);
        return new ResponseEntity<>(courseDto,HttpStatus.OK);
    }

}
