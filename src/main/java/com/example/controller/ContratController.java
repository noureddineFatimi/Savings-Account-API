package com.example.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Exceptions.GlobalException.ResourceNotFoundException;
import com.example.dto.ContratResponseDTO;
import com.example.model.Contrat;
import com.example.model.Prelevement;
import com.example.repository.ContratRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import com.example.dto.ContratRequestDTO;


//DTO: Validtaion de donnees + couche intermediaire  + affichage de donnes specifique à partir de lui à la place du modèle
@RestController
@RequestMapping("api/Contrat")
public class ContratController {

    @Autowired
    private ContratRepository ContratRepository;

    @GetMapping("/GetAllContrats")
    public ResponseEntity<List<Contrat>> getAllContrats(){
        List<Contrat> listeContrats = ContratRepository.findAll();
        return ResponseEntity.ok(listeContrats);
    }

    @GetMapping("/GetContratById/{id}")
    public ResponseEntity<ContratResponseDTO> getContratById(@PathVariable Long id) {
        Optional<Contrat> contratOptional = ContratRepository.findById(id);
        if (!contratOptional.isPresent()) {
            throw new ResourceNotFoundException("Contrat not found with id " + id);
        }
        Contrat contrat = contratOptional.get();
        Prelevement prelevement = contrat.getPrelevement();
        ContratResponseDTO responseDTO = new ContratResponseDTO();
        responseDTO.setContrat(contrat);
        responseDTO.setPrelevement(prelevement);
        return ResponseEntity.ok(responseDTO);
    }  

    @PostMapping("/AddContrat")
    public ResponseEntity<Contrat> postContrat(@Valid @RequestBody ContratRequestDTO contratRequestDTO){
        Contrat contrat = new Contrat();
        contrat.setLibContrat(contratRequestDTO.getLibContrat());
        Contrat contratSaved = ContratRepository.save(contrat);
        return ResponseEntity.ok(contratSaved);
    }

    @Operation(summary = "Laisser les elements non modifiés vides, null ou blancs")
    @PutMapping("/UpdateContratById/{id}")
    public ResponseEntity<Contrat> putContrat(@PathVariable Long id, @RequestBody ContratRequestDTO contratRequestDTO){
        Optional<Contrat> optionlaContrat = ContratRepository.findById(id);

        if (!optionlaContrat.isPresent()) {
            throw new ResourceNotFoundException("Contrat not found with id " + id);
        }

        Contrat contratToUpdate = optionlaContrat.get();
       if(contratRequestDTO.getLibContrat() != null && contratRequestDTO.getLibContrat().isBlank()){
        contratToUpdate.setLibContrat(contratRequestDTO.getLibContrat());
       }
       Contrat updatedContrat=ContratRepository.save(contratToUpdate);
       return ResponseEntity.ok(updatedContrat);
    }

    @DeleteMapping("/DeleteContratByIdAndTheAssociatedPrelevement/{id}")
    public ResponseEntity<ContratResponseDTO> DeleteContratByIdAndTheAssociatedPrelevement(@Parameter(description = "Id of Contrat") @PathVariable Long id){
        Optional<Contrat> optionlaContrat = ContratRepository.findById(id);

        if (!optionlaContrat.isPresent()) {
            throw new ResourceNotFoundException("Contrat not found with id " + id);
        }
        Contrat contrat = optionlaContrat.get();

        ContratResponseDTO contratResponseDTO = new ContratResponseDTO();
        contratResponseDTO.setContrat(contrat);
        contratResponseDTO.setPrelevement(contrat.getPrelevement());
        ContratRepository.deleteById(id);
        return ResponseEntity.ok(contratResponseDTO);
    }

}