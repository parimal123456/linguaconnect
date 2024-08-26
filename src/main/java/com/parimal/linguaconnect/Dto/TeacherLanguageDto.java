package com.parimal.linguaconnect.Dto;

import com.parimal.linguaconnect.entity.Language;
import com.parimal.linguaconnect.entity.Level;
import com.parimal.linguaconnect.entity.Teacher;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherLanguageDto {
    private String language;
    private String level;
    private String pricePerHour;
}
