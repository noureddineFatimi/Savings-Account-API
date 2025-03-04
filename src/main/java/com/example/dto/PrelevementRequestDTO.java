package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrelevementRequestDTO {
    
    @NotNull(message = "Date input is null")
    private LocalDate dateDePrelevement;
    
    @NotNull(message = "Time input is Null")
    @Schema(description = "Time format: HH:mm:ss", example = "14:30:00")
    @JsonFormat(shape =  JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime tempsDePrelevement;

    @NotNull(message = "Amount input is null")
    private BigDecimal montant;

}
