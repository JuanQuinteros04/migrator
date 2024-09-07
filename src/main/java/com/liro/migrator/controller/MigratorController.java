package com.liro.migrator.controller;

import com.liro.migrator.service.BreedsMigrator;
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
    private final BreedsMigrator breedsMigrator;


    @Autowired
    public MigratorController(ClientsMigrator clientsMigrator, BreedsMigrator breedsMigrator) {
        this.clientsMigrator = clientsMigrator;
        this.breedsMigrator = breedsMigrator;
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> migrateUsers(@RequestPart(required = true) MultipartFile file,
                                             @RequestParam("vetUserId") Long vetUserId) throws IOException {
        clientsMigrator.migrate(vetUserId, file);

        return ResponseEntity.status(200).build();
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> migrateAnimals(@RequestPart(required = true) MultipartFile file,
                                             @RequestParam("vetUserId") Long vetUserId) throws IOException {
        clientsMigrator.migrate(vetUserId, file);

        return ResponseEntity.status(200).build();
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> migrateBreeds(@RequestPart(required = true) MultipartFile file) throws IOException {
        breedsMigrator.migrate(file);

        return ResponseEntity.status(200).build();
    }
}