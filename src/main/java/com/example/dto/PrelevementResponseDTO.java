package com.example.dto;

import com.example.model.Contrat;
import lombok.Data;

@Data
public class PrelevementResponseDTO {
    private  PrelevementObjectWithTimeByZone prelevement;
    private Contrat contrat;

}
