package com.liro.migrator.config;


import com.liro.migrator.dtos.ClientRegister;
import com.liro.migrator.dtos.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "users-service")
public interface FeignUserClient {

    @RequestMapping(method = RequestMethod.POST, value = "/users/clients")
    ResponseEntity<List<UserResponse>> createClients(@RequestBody List<ClientRegister> clientRegisters,
                                                     @RequestParam("vetClinicId") Long vetClinicId);
}
