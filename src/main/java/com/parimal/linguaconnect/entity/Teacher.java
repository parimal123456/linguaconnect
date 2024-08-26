package com.parimal.linguaconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

@Entity
@Table(name = "teacher")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private UserInfo userInfo;
    @Column(nullable = false)
    private int experience;

   

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "teacher_language",
               joinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "language_id", referencedColumnName = "id"))
    private List<Language> languages;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeacherLanguage> teacherLanguages;
}
