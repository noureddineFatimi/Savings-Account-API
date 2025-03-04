package com.example.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContratRequestDTO {
    
    @NotNull(message = "Empty contrat object or values in the passed object that can be null are null")
    @Size(min = 1)
    private String libContrat;
}
