package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Contrat;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long>{
    
}