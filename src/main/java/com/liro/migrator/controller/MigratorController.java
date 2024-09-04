package com.liro.migrator.controller;

import com.liro.migrator.service.ClientsMigrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/migrator")
public class MigratorController {

    private final ClientsMigrator clientsMigrator;

    @Autowired
    public MigratorController(ClientsMigrator clientsMigrator) {
        this.clientsMigrator = clientsMigrator;
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createRecord(@RequestPart(required = true) MultipartFile file,
                                             @RequestParam("vetUserId") Long vetUserId) throws IOException {
        clientsMigrator.migrate(vetUserId, file);

        return ResponseEntity.status(200).build();
    }
}