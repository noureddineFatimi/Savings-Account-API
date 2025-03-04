package com.example.dto;

import java.time.LocalTime;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PrelevementObjectWithTimeByZone {

    private Long idPrélèvement;

    private BigDecimal montant;

    private LocalDate dateDePrelevement;

    private LocalTime tempsDePrelevement;

    private String fuseauHoraire;

}
