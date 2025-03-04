package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.dto.PrelevementObjectWithTimeByZone;
import com.example.dto.PrelevementRequestDTO;
import com.example.Exceptions.GlobalException.ResourceNotFoundException;
import com.example.dto.PrelevementResponseDTO;
import com.example.model.Contrat;
import com.example.model.Prelevement;
import com.example.repository.ContratRepository;
import com.example.repository.PrelevementRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import com.example.Exceptions.GlobalException.HoureException;
import com.example.Exceptions.GlobalException.AlreadyExistException;

@Slf4j
@RestController
@RequestMapping("api/prelevement")
public class PrelevementController {

    @Autowired
    private PrelevementRepository PrelevementRepository;

    @Autowired
    private ContratRepository contratRepository;

    @GetMapping("/GetAllPrelevements")
    public ResponseEntity<List<PrelevementResponseDTO>> getAllPrelevements(@Parameter(description = "Fuseau horaire: e.g.,GMT+01:00") @RequestHeader("Time-Zone") String timeZone) {
        List<Prelevement> listPrelevements = PrelevementRepository.findAll();
        List<PrelevementResponseDTO> listPrelevementsDTOs = new ArrayList<>();
        ZoneId zoneId = ZoneId.of(timeZone);
        for (Prelevement prelevement : listPrelevements) {
            PrelevementResponseDTO prelevementResponseDTO = new PrelevementResponseDTO();
            PrelevementObjectWithTimeByZone prelevementObjectWithTimeByZone = new PrelevementObjectWithTimeByZone();
            LocalDate dateStokee = prelevement.getDateDePrelevement();
            LocalTime tempsStockee =  prelevement.getTempsDePrelevement();
            ZonedDateTime zonedDateTime = ZonedDateTime.of(dateStokee,tempsStockee,ZoneId.of("GMT+01:00")); 
            ZonedDateTime zonedDateTimeEnFuseauHoraireDemande = zonedDateTime.withZoneSameInstant(zoneId);
            LocalDate dateEnFuseauHoraireDemande = zonedDateTimeEnFuseauHoraireDemande.toLocalDate();
            LocalTime timeEnFuseauHoraireDemande = zonedDateTimeEnFuseauHoraireDemande.toLocalTime();
            prelevementObjectWithTimeByZone.setIdPrélèvement(prelevement.getIdPrélèvement());
            prelevementObjectWithTimeByZone.setMontant(prelevement.getMontant());
            prelevementObjectWithTimeByZone.setDateDePrelevement(dateEnFuseauHoraireDemande);
            prelevementObjectWithTimeByZone.setTempsDePrelevement(timeEnFuseauHoraireDemande);
            prelevementObjectWithTimeByZone.setFuseauHoraire(timeZone);
            prelevementResponseDTO.setPrelevement(prelevementObjectWithTimeByZone);
            prelevementResponseDTO.setContrat(prelevement.getContrat());
            listPrelevementsDTOs.add(prelevementResponseDTO);
        }
        return ResponseEntity.ok(listPrelevementsDTOs);
    }

    @GetMapping("/GetPrelevementsByDate/{dateDePrelevement}")
    public ResponseEntity<List<PrelevementResponseDTO>> getPrelevementsByDate(@Parameter(description = "Date format: yyyy-MM-dd") @PathVariable LocalDate dateDePrelevement,@Parameter(description = "Fuseau horaire: e.g.,GMT+01:00")@RequestHeader("Time-Zone") String timeZone) {
        ZoneId requestedZoneId = ZoneId.of(timeZone);
        List<Prelevement> allPrelevements = PrelevementRepository.findAll();
        List<PrelevementResponseDTO> responseDTOs = new ArrayList<>();
        for (Prelevement prelevement : allPrelevements) {
            ZonedDateTime zonedDateTimeStockee = ZonedDateTime.of(prelevement.getDateDePrelevement(), prelevement.getTempsDePrelevement(), ZoneId.of("GMT+01:00"));
            ZonedDateTime zonedDateTimeEnFuseauHoraireDemande = zonedDateTimeStockee.withZoneSameInstant(requestedZoneId);
            LocalDate dateConvertie = zonedDateTimeEnFuseauHoraireDemande.toLocalDate();
            LocalTime heureConvertie = zonedDateTimeEnFuseauHoraireDemande.toLocalTime();
            if (dateConvertie.equals(dateDePrelevement)) {
                PrelevementObjectWithTimeByZone prelevementObjectWithTimeByZone = new PrelevementObjectWithTimeByZone();
                prelevementObjectWithTimeByZone.setIdPrélèvement(prelevement.getIdPrélèvement());
                prelevementObjectWithTimeByZone.setMontant(prelevement.getMontant());
                prelevementObjectWithTimeByZone.setDateDePrelevement(dateConvertie);
                prelevementObjectWithTimeByZone.setTempsDePrelevement(heureConvertie);
                prelevementObjectWithTimeByZone.setFuseauHoraire(timeZone);
                PrelevementResponseDTO prelevementResponseDTO = new PrelevementResponseDTO();
                prelevementResponseDTO.setPrelevement(prelevementObjectWithTimeByZone);
                prelevementResponseDTO.setContrat(prelevement.getContrat());
                responseDTOs.add(prelevementResponseDTO);
            }
        }
    
        return ResponseEntity.ok(responseDTOs);
    }
    

    @GetMapping("GetPrelevementsByDateAndHour/{dateDePrelevement}/{heureDePrelevement}")
    public ResponseEntity<List<PrelevementResponseDTO>> getPrelevementByDateAndHour(@Parameter(description = "Fuseau horaire: e.g.,GMT+01:00") @RequestHeader("Time-Zone") String timeZone, @Parameter(description = "Date format: \t yyyy-mm-dd") @PathVariable LocalDate dateDePrelevement, @PathVariable Integer heureDePrelevement) {

        if (heureDePrelevement < 0 || heureDePrelevement > 23) {
            throw new HoureException("Heure entré invalide: " +  heureDePrelevement);
        }

        ZoneId zoneId = ZoneId.of(timeZone);
        LocalDate dateInput = dateDePrelevement;
        LocalTime timeInput =  LocalTime.of(heureDePrelevement, 0, 0, 0);
        ZonedDateTime zonedDateTimeInput = ZonedDateTime.of(dateInput,timeInput,zoneId); 
        ZonedDateTime zonedDateTimeEnFuseauHoraireDemandeInput = zonedDateTimeInput.withZoneSameInstant(ZoneId.of("GMT+01:00"));
        LocalDate dateEnFuseauHoraireDemandeInput = zonedDateTimeEnFuseauHoraireDemandeInput.toLocalDate();
        LocalTime timeEnFuseauHoraireDemandeInput = zonedDateTimeEnFuseauHoraireDemandeInput.toLocalTime();
        

        List<Prelevement> listPrelevements = PrelevementRepository.findBydateDePrelevement(dateEnFuseauHoraireDemandeInput);
        
        List<PrelevementResponseDTO> listPrelevementsDTOs = listPrelevements.stream()
        .filter(prelevement -> prelevement.getTempsDePrelevement().getHour() == timeEnFuseauHoraireDemandeInput.getHour())
        .map(prelevement -> {
            PrelevementResponseDTO prelevementResponseDTO = new PrelevementResponseDTO();
            PrelevementObjectWithTimeByZone prelevementObjectWithTimeByZone = new PrelevementObjectWithTimeByZone();
            
            LocalDate dateStokee = prelevement.getDateDePrelevement();
            LocalTime tempsStockee =  prelevement.getTempsDePrelevement();
            ZonedDateTime zonedDateTime = ZonedDateTime.of(dateStokee,tempsStockee,ZoneId.of("GMT+01:00")); 
            ZonedDateTime zonedDateTimeEnFuseauHoraireDemande = zonedDateTime.withZoneSameInstant(zoneId);
            LocalDate dateEnFuseauHoraireDemande = zonedDateTimeEnFuseauHoraireDemande.toLocalDate();
            LocalTime timeEnFuseauHoraireDemande = zonedDateTimeEnFuseauHoraireDemande.toLocalTime();
            prelevementObjectWithTimeByZone.setIdPrélèvement(prelevement.getIdPrélèvement());
            prelevementObjectWithTimeByZone.setMontant(prelevement.getMontant());
            prelevementObjectWithTimeByZone.setDateDePrelevement(dateEnFuseauHoraireDemande);
            prelevementObjectWithTimeByZone.setTempsDePrelevement(timeEnFuseauHoraireDemande);
            prelevementObjectWithTimeByZone.setFuseauHoraire(timeZone);
            prelevementResponseDTO.setPrelevement(prelevementObjectWithTimeByZone);
            prelevementResponseDTO.setContrat(prelevement.getContrat());
            return prelevementResponseDTO;
        })
        .collect(Collectors.toList());
        return ResponseEntity.ok(listPrelevementsDTOs);
    }

    @GetMapping("/GetPrelevementsByDateAndTime/{dateDePrelevement}/{tempsDePrelevement}")
    public ResponseEntity<List<PrelevementResponseDTO>> getPrelevementsByDateAndTime(@Parameter(description = "Fuseau horaire: e.g.,GMT+01:00") @RequestHeader("Time-Zone") String timeZone, @Parameter(description = "Date format: \t yyyy-mm-dd") @PathVariable LocalDate dateDePrelevement,   @Parameter(description = "Time format: \t hh:mm:ss", schema = @Schema(type = "string", format = "time")) @PathVariable LocalTime tempsDePrelevement) {

        ZoneId zoneId = ZoneId.of(timeZone);
        LocalDate dateInput = dateDePrelevement;
        LocalTime timeInput =  tempsDePrelevement;
        ZonedDateTime zonedDateTimeInput = ZonedDateTime.of(dateInput,timeInput,zoneId); 
        ZonedDateTime zonedDateTimeEnFuseauHoraireDemandeInput = zonedDateTimeInput.withZoneSameInstant(ZoneId.of("GMT+01:00"));
        LocalDate dateEnFuseauHoraireDemandeInput = zonedDateTimeEnFuseauHoraireDemandeInput.toLocalDate();
        LocalTime timeEnFuseauHoraireDemandeInput = zonedDateTimeEnFuseauHoraireDemandeInput.toLocalTime();

        List<Prelevement> listPrelevements = PrelevementRepository.findByDateDePrelevementAndTempsDePrelevement(dateEnFuseauHoraireDemandeInput, timeEnFuseauHoraireDemandeInput);
        List<PrelevementResponseDTO> listPrelevementsDTOs = listPrelevements.stream()
        .map(prelevement -> {
            PrelevementResponseDTO prelevementResponseDTO = new PrelevementResponseDTO();
            PrelevementObjectWithTimeByZone prelevementObjectWithTimeByZone = new PrelevementObjectWithTimeByZone();
            LocalDate dateStokee = prelevement.getDateDePrelevement();
            LocalTime tempsStockee =  prelevement.getTempsDePrelevement();
            ZonedDateTime zonedDateTime = ZonedDateTime.of(dateStokee,tempsStockee,ZoneId.of("GMT+01:00")); 
            ZonedDateTime zonedDateTimeEnFuseauHoraireDemande = zonedDateTime.withZoneSameInstant(zoneId);
            LocalDate dateEnFuseauHoraireDemande = zonedDateTimeEnFuseauHoraireDemande.toLocalDate();
            LocalTime timeEnFuseauHoraireDemande = zonedDateTimeEnFuseauHoraireDemande.toLocalTime();
            prelevementObjectWithTimeByZone.setIdPrélèvement(prelevement.getIdPrélèvement());
            prelevementObjectWithTimeByZone.setMontant(prelevement.getMontant());
            prelevementObjectWithTimeByZone.setDateDePrelevement(dateEnFuseauHoraireDemande);
            prelevementObjectWithTimeByZone.setTempsDePrelevement(timeEnFuseauHoraireDemande);
            prelevementObjectWithTimeByZone.setFuseauHoraire(timeZone);
            prelevementResponseDTO.setPrelevement(prelevementObjectWithTimeByZone);            
            prelevementResponseDTO.setContrat(prelevement.getContrat());
            return prelevementResponseDTO;
        })
        .collect(Collectors.toList());
        return ResponseEntity.ok(listPrelevementsDTOs);
    }

    @GetMapping("GetPrelevementByIdContrat/{id}") //
    public ResponseEntity<PrelevementResponseDTO> getPrelevementByIdContrat(@PathVariable Long id,@Parameter(description = "Fuseau horaire: e.g.,GMT+01:00") @RequestHeader("Time-Zone") String timeZone) {
        Optional<Contrat> contrat = contratRepository.findById(id);
        
        if(!contrat.isPresent()){
            throw new ResourceNotFoundException("Prelevement not found with id Contrat " + id);
        }

        Prelevement prelevement = contrat.get().getPrelevement();

        PrelevementResponseDTO prelevementResponseDTO = new PrelevementResponseDTO();
        prelevementResponseDTO.setContrat(prelevement.getContrat());

        ZoneId zoneId = ZoneId.of(timeZone);
        LocalDate dateStokee = prelevement.getDateDePrelevement();
        LocalTime tempsStockee =  prelevement.getTempsDePrelevement();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateStokee,tempsStockee,ZoneId.of("GMT+01:00")); 
        ZonedDateTime zonedDateTimeEnFuseauHoraireDemande = zonedDateTime.withZoneSameInstant(zoneId);
        log.info("ZonedDateTime in requested time zone: {}", zonedDateTimeEnFuseauHoraireDemande);

        LocalDate dateEnFuseauHoraireDemande = zonedDateTimeEnFuseauHoraireDemande.toLocalDate();
        LocalTime timeEnFuseauHoraireDemande = zonedDateTimeEnFuseauHoraireDemande.toLocalTime();

        PrelevementObjectWithTimeByZone prelevementObjectWithTimeByZone = new PrelevementObjectWithTimeByZone();
        prelevementObjectWithTimeByZone.setIdPrélèvement(prelevement.getIdPrélèvement());
        prelevementObjectWithTimeByZone.setMontant(prelevement.getMontant());
        prelevementObjectWithTimeByZone.setDateDePrelevement(dateEnFuseauHoraireDemande);
        prelevementObjectWithTimeByZone.setTempsDePrelevement(timeEnFuseauHoraireDemande);
        prelevementObjectWithTimeByZone.setFuseauHoraire(timeZone);
        
        prelevementResponseDTO.setPrelevement(prelevementObjectWithTimeByZone);
       
        return ResponseEntity.ok(prelevementResponseDTO);
    }  

    @PostMapping("/AddPrelevementByIdContrat/{id}")
    public ResponseEntity<PrelevementResponseDTO> postPrelevementByContrat(@Valid @RequestBody PrelevementRequestDTO prelevementRequestDTO, @Parameter(description = "Fuseau horaire: e.g.,GMT+01:00") @RequestHeader("Time-Zone") String timeZone, @PathVariable Long id){

        Optional<Contrat> contrat = contratRepository.findById(id);

        if(!contrat.isPresent()){
            throw new ResourceNotFoundException("Contrat not found with id  " + id);
        }

        if(contrat.get().getPrelevement() != null) {
            throw new AlreadyExistException("Contrat linked with another prelevement");
        }

        ZoneId zoneId = ZoneId.of(timeZone);
        LocalDate dateRequest = prelevementRequestDTO.getDateDePrelevement();
        LocalTime tempsRequest =  prelevementRequestDTO.getTempsDePrelevement();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateRequest,tempsRequest,zoneId);
        ZonedDateTime zonedDateTimeInGmtPlusOneHour = zonedDateTime.withZoneSameInstant(ZoneId.of("GMT+01:00")); 

        LocalDate date = zonedDateTimeInGmtPlusOneHour.toLocalDate();
        LocalTime timeInGmtPlusOneHour = zonedDateTimeInGmtPlusOneHour.toLocalTime();

        Prelevement prelevement =new Prelevement();
       

        prelevement.setContrat(contrat.get());
        prelevement.setDateDePrelevement(date);
        prelevement.setMontant(prelevementRequestDTO.getMontant());
        prelevement.setTempsDePrelevement(timeInGmtPlusOneHour);

        Prelevement savedPrelevementObj = PrelevementRepository.save(prelevement);

        PrelevementResponseDTO savedPrelevementResponseDTO = new PrelevementResponseDTO();
        
        PrelevementObjectWithTimeByZone prelevementObjectWithTimeByZone = new PrelevementObjectWithTimeByZone();
        prelevementObjectWithTimeByZone.setIdPrélèvement(savedPrelevementObj.getIdPrélèvement());
        prelevementObjectWithTimeByZone.setMontant(savedPrelevementObj.getMontant());
        prelevementObjectWithTimeByZone.setDateDePrelevement(dateRequest);
        prelevementObjectWithTimeByZone.setTempsDePrelevement(tempsRequest);
        prelevementObjectWithTimeByZone.setFuseauHoraire(timeZone);
        
        savedPrelevementResponseDTO.setPrelevement(prelevementObjectWithTimeByZone);

        savedPrelevementResponseDTO.setContrat(savedPrelevementObj.getContrat());
        return ResponseEntity.ok(savedPrelevementResponseDTO);
    }

    @Operation(summary = "Laisser les elements non modifiés vides, null ou blancs")
    @PutMapping("/UpdatePrelevementById/{id}")
    public ResponseEntity<PrelevementResponseDTO> putPrelevementById(@PathVariable Long id, @RequestBody PrelevementRequestDTO prelevementRequestDTO, @Parameter(description = "Fuseau horaire: e.g.,GMT+01:00") @RequestHeader(value = "Time-Zone", required = false) String timeZone){

        Optional<Prelevement> prelevement = PrelevementRepository.findById(id);

        if(!prelevement.isPresent()){
            throw new ResourceNotFoundException("Prelevement not found with id " + id);
        }

        if(timeZone != null){
            
            if(prelevementRequestDTO.getTempsDePrelevement()!=null){

                ZoneId zoneId = ZoneId.of(timeZone);
                LocalDate dateRequest = prelevement.get().getDateDePrelevement();
                LocalTime tempsRequest =  prelevementRequestDTO.getTempsDePrelevement();
                ZonedDateTime zonedDateTime = ZonedDateTime.of(dateRequest,tempsRequest,zoneId);
                ZonedDateTime zonedDateTimeInGmtPlusOneHour = zonedDateTime.withZoneSameInstant(ZoneId.of("GMT+01:00")); 
        
                LocalDate date = zonedDateTimeInGmtPlusOneHour.toLocalDate();
                LocalTime timeInGmtPlusOneHour = zonedDateTimeInGmtPlusOneHour.toLocalTime();

                prelevement.get().setDateDePrelevement(date);
                prelevement.get().setTempsDePrelevement(timeInGmtPlusOneHour);

            }

            
        }
       

        if(prelevementRequestDTO.getDateDePrelevement() != null){
            prelevement.get().setDateDePrelevement(prelevementRequestDTO.getDateDePrelevement());
        }

        if(prelevementRequestDTO.getMontant() != null){
            prelevement.get().setMontant(prelevementRequestDTO.getMontant());
        }

        Prelevement savedPrelevementObj = PrelevementRepository.save(prelevement.get());

        PrelevementResponseDTO updatedPrelevementResponseDTO = new PrelevementResponseDTO();

        PrelevementObjectWithTimeByZone prelevementObjectWithTimeByZone = new PrelevementObjectWithTimeByZone();
        ZoneId zoneId = ZoneId.of(timeZone);
        LocalDate dateStokee = savedPrelevementObj.getDateDePrelevement();
        LocalTime tempsStockee =  savedPrelevementObj.getTempsDePrelevement();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateStokee,tempsStockee,ZoneId.of("GMT+01:00")); 
        ZonedDateTime zonedDateTimeEnFuseauHoraireDemande = zonedDateTime.withZoneSameInstant(zoneId);
        LocalDate dateEnFuseauHoraireDemande = zonedDateTimeEnFuseauHoraireDemande.toLocalDate();
        LocalTime timeEnFuseauHoraireDemande = zonedDateTimeEnFuseauHoraireDemande.toLocalTime();
        prelevementObjectWithTimeByZone.setIdPrélèvement(savedPrelevementObj.getIdPrélèvement());
        prelevementObjectWithTimeByZone.setMontant(savedPrelevementObj.getMontant());
        prelevementObjectWithTimeByZone.setDateDePrelevement(dateEnFuseauHoraireDemande);
        prelevementObjectWithTimeByZone.setTempsDePrelevement(timeEnFuseauHoraireDemande);
        prelevementObjectWithTimeByZone.setFuseauHoraire(timeZone);

        updatedPrelevementResponseDTO.setPrelevement(prelevementObjectWithTimeByZone);
        updatedPrelevementResponseDTO.setContrat(savedPrelevementObj.getContrat());

        return ResponseEntity.ok(updatedPrelevementResponseDTO);
    }

    @DeleteMapping("/DeletePrelevementById/{id}")
    public ResponseEntity<PrelevementObjectWithTimeByZone> deletePrelevementById(@PathVariable Long id, @Parameter(description = "Fuseau horaire: e.g.,GMT+01:00") @RequestHeader(value = "Time-Zone") String timeZone){
        Optional<Prelevement> prelevement = PrelevementRepository.findById(id);
        if(!prelevement.isPresent()){
            throw new ResourceNotFoundException("Prelevement not found with id " + id);
        }

        PrelevementRepository.deleteById(id);

        PrelevementObjectWithTimeByZone prelevementObjectWithTimeByZone = new PrelevementObjectWithTimeByZone();
        ZoneId zoneId = ZoneId.of(timeZone);
        LocalDate dateStokee = prelevement.get().getDateDePrelevement();
        LocalTime tempsStockee =  prelevement.get().getTempsDePrelevement();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateStokee,tempsStockee,ZoneId.of("GMT+01:00")); 
        ZonedDateTime zonedDateTimeEnFuseauHoraireDemande = zonedDateTime.withZoneSameInstant(zoneId);
        LocalDate dateEnFuseauHoraireDemande = zonedDateTimeEnFuseauHoraireDemande.toLocalDate();
        LocalTime timeEnFuseauHoraireDemande = zonedDateTimeEnFuseauHoraireDemande.toLocalTime();
        prelevementObjectWithTimeByZone.setIdPrélèvement(prelevement.get().getIdPrélèvement());
        prelevementObjectWithTimeByZone.setMontant(prelevement.get().getMontant());
        prelevementObjectWithTimeByZone.setDateDePrelevement(dateEnFuseauHoraireDemande);
        prelevementObjectWithTimeByZone.setTempsDePrelevement(timeEnFuseauHoraireDemande);
        prelevementObjectWithTimeByZone.setFuseauHoraire(timeZone);

        return ResponseEntity.ok(prelevementObjectWithTimeByZone);
    }
}