package com.liro.migrator.controller;

import com.liro.migrator.dtos.MigratorRequest;
import com.liro.migrator.service.BreedsMigrator;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Queue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
public class MigratorController {

    private final Queue<MigratorRequest> migratorQueue;
    private final BreedsMigrator breedsMigrator;

    public MigratorController(Queue<MigratorRequest> migratorQueue, BreedsMigrator breedsMigrator) {
        this.migratorQueue = migratorQueue;
        this.breedsMigrator = breedsMigrator;
    }


    @PostMapping(value = "/breeds", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> migrateBreeds(@RequestPart(required = true) MultipartFile file) throws IOException {
        breedsMigrator.migrate(file);

        return ResponseEntity.status(200).build();
    }
}