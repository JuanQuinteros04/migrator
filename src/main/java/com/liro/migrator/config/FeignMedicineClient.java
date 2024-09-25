package com.liro.migrator.config;

import com.liro.migrator.dtos.AnimalDTO;
import com.liro.migrator.dtos.AnimalMigrationResponse;
import com.liro.migrator.dtos.BreedDTO;
import com.liro.migrator.dtos.MedicineDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "medicines-service")
public interface FeignMedicineClient {

    @RequestMapping(method = RequestMethod.POST, value = "/medicines/migrate")
    ResponseEntity<Void> createMedicines(@RequestBody List<MedicineDTO> medicineDTOS);

}
