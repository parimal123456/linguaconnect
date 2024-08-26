package com.parimal.linguaconnect.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PricePerHourDto {
    private String language;
    private String level;
    private String pricePerHour;
}
