package com.parimal.linguaconnect.Dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherDto {
    
    private String username;
    private int experience;
    List<String> languages;

}
