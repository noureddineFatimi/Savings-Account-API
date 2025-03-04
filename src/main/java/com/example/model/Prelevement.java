package com.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "prélèvement")
public class Prelevement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_prélèvement")
    private Long idPrélèvement;

    @Column(name = "Date_De_Prélèvement")
    private LocalDate dateDePrelevement;

    @Column(name = "Temps_De_Prelevement")
    private LocalTime tempsDePrelevement;

    @Column(name = "montant")
    private BigDecimal montant;

    @OneToOne
    @JoinColumn(name = "contrat_id_Contrat") //la cle etrangere pour determiner le contrat associé 
    @JsonIgnore
    private Contrat contrat;
}