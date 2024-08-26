package com.parimal.linguaconnect.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherLanguageCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacherLanguage_id", nullable = false)
    private TeacherLanguage teacherLanguage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessionLength_id", nullable = false)
    private SessionLength sessionLength;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @ManyToMany(mappedBy = "teacherLanguageCourses", fetch = FetchType.LAZY)
    private List<Student> students;

    
    private double price;

}
