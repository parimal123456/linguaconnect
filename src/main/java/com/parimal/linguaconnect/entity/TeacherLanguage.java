package com.parimal.linguaconnect.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teacherLanguage")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @Column(nullable = false)
    private double pricePerHour;

    @OneToMany(mappedBy = "teacherLanguage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeacherLanguageCourse> TeacherLanguageCourses;
}
