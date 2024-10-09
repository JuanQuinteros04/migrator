package com.liro.migrator.config;

import com.liro.migrator.dtos.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "medicines-service")
public interface FeignMedicineClient {

    @RequestMapping(method = RequestMethod.POST, value = "/medicines/migrate")
    ResponseEntity<Void> createMedicines(@RequestBody List<MedicineDTO> medicineDTOS);

    @RequestMapping(method = RequestMethod.POST, value = "/applications/migrate")
    ResponseEntity<Void> createApplications(@RequestBody List<ApplicationRecordDTO> applicationRecordDTO);

}
