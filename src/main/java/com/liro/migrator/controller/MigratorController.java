package com.liro.migrator.controller;

import com.liro.migrator.dtos.MigratorRequest;
import com.liro.migrator.dtos.UserResponse;
import com.liro.migrator.service.AnimalsMigrator;
import com.liro.migrator.service.BreedsMigrator;
import com.liro.migrator.service.ClientsMigrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Queue;

@RestController
@RequestMapping("/migrator")
public class MigratorController {

    private final BreedsMigrator breedsMigrator;


    private final Queue<MigratorRequest> migratorQueue;




    @Autowired
    public MigratorController(BreedsMigrator breedsMigrator, Queue<MigratorRequest> migratorQueue) {
        this.breedsMigrator = breedsMigrator;
        this.migratorQueue  = migratorQueue;
    }

    @PostMapping(value = "/users", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> migrateUsers(@RequestPart(required = true, value = "users") MultipartFile usersFile,
                                             @RequestPart(required = true, value = "animals") MultipartFile animalsFile,

                                             @RequestParam("vetUserId") Long vetUserId) throws IOException {


        System.out.println("Llego migracion");
        migratorQueue.add(MigratorRequest.builder().animalsFile(animalsFile).usersFile(usersFile).vetUserId(vetUserId).build());

        return ResponseEntity.status(200).build();
    }

    @PostMapping(value = "/breeds", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> migrateBreeds(@RequestPart(required = true) MultipartFile file) throws IOException {
        breedsMigrator.migrate(file);

        return ResponseEntity.status(200).build();
    }
}