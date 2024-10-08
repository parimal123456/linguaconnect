package com.parimal.linguaconnect.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parimal.linguaconnect.Dto.SubscribeDto;
import com.parimal.linguaconnect.Service.UserInfoService;
import com.parimal.linguaconnect.entity.Student;
import com.parimal.linguaconnect.entity.Course;
import com.parimal.linguaconnect.entity.UserInfo;
import com.parimal.linguaconnect.repository.StudentRepository;
import com.parimal.linguaconnect.repository.CourseRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/student/")
public class StudentController {

    private final UserInfoService userInfoService;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    @PostMapping("subscribe")
    public ResponseEntity<?> subscribeToCourse(@RequestBody SubscribeDto subscribeDto,Authentication authentication){
        UserDetails userDetails=(UserDetails)authentication.getPrincipal();
        UserInfo userInfo=userInfoService.findByEmail(userDetails.getUsername()).orElseThrow(()->new UsernameNotFoundException("user not registered"));
        Course course=courseRepository.findById(Long.valueOf(subscribeDto.getId())).orElseThrow(()->new UsernameNotFoundException("Course Not present"));
        Student student=studentRepository.findByUserInfoId(userInfo.getId()).orElseThrow(()->new UsernameNotFoundException("user not enrolled as student"));
        course.getStudents().add(student);
        student.getCourses().add(course);

        courseRepository.save(course);
        studentRepository.save(student);

        return new ResponseEntity<>("Successfully registered for the Course",HttpStatus.OK);
    }
}
