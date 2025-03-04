package com.example.dto;

import com.example.model.Contrat;
import com.example.model.Prelevement;
import lombok.Data;

@Data
public class ContratResponseDTO {

    private Contrat contrat;

    private Prelevement prelevement;

}