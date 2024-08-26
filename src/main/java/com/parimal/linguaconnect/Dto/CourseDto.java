package com.parimal.linguaconnect.Dto;

import lombok.Builder;
import lombok.Data;
import java.util.*;
@Data
@Builder
public class CourseDto {
    private String language;
    private String level;
    private List<String> sessionLengths;
    private Map<String,String> slots;
}
