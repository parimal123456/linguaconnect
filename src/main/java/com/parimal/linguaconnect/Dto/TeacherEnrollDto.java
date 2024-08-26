package com.parimal.linguaconnect.Dto;

import java.util.List;

import lombok.Data;

@Data
public class TeacherEnrollDto {
    private int experience;
    List<String> languages;
}
