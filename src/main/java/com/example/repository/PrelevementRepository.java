package com.example.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.model.Prelevement;

@Repository
public interface PrelevementRepository extends JpaRepository<Prelevement, Long>{
    
    List<Prelevement> findBydateDePrelevement(LocalDate dateDePrelevement);
    List<Prelevement> findByDateDePrelevementAndTempsDePrelevement(LocalDate dateDePrelevement, LocalTime tempsDePrelevement);    
}
