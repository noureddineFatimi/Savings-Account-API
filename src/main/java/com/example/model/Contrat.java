package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="contrat")
public class Contrat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Contrat")
    private Long idContrat;


    @Column(name = "Libellé_Contrat")
    private String libContrat;

    @OneToOne(mappedBy = "contrat") // Gère la sérialisation de la partie "maître" de la relation
    @JsonIgnore // Ignore la sérialisation de cette propriété
    private Prelevement prelevement;
}