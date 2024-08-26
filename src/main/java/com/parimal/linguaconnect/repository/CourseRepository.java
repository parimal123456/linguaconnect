package com.parimal.linguaconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parimal.linguaconnect.entity.Course;

public interface CourseRepository extends JpaRepository<Course,Long> {

}
