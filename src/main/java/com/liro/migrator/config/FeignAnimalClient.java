package com.liro.migrator.config;

import com.liro.migrator.dtos.AnimalDTO;
import com.liro.migrator.dtos.AnimalMigrationResponse;
import com.liro.migrator.dtos.BreedDTO;
import com.liro.migrator.dtos.ClientRegister;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "animals-service")
public interface FeignAnimalClient {

    @RequestMapping(method = RequestMethod.POST, value = "/animals/migrate")
    ResponseEntity<List<AnimalMigrationResponse>> createAnimals(@RequestBody List<AnimalDTO> animalDTOS,
                                                                @RequestParam("vetClinicId") Long vetClinicId);

    @RequestMapping(method = RequestMethod.POST, value = "/breeds")
    ResponseEntity<Void> createBreeds(@RequestBody List<BreedDTO> breedDTOS);
}
