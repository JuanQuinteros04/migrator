package com.liro.migrator.config;

import com.liro.migrator.dtos.BreedDTO;
import com.liro.migrator.dtos.ConsultationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "consultations-service")
public interface FeignConsultationsClient {

    @RequestMapping(method = RequestMethod.POST, value = "/consultations/migrate")
    ResponseEntity<Void> createConsultations(@RequestBody List<ConsultationDTO> consultationDTOS, @RequestParam(name = "vetUserId") Long vetUserId);
}
