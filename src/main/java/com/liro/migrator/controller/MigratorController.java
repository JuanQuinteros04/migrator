package com.liro.migrator.controller;

import com.liro.migrator.service.BreedsMigrator;
import com.liro.migrator.service.MedicinesMigrator;
import com.opencsv.exceptions.CsvException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class MigratorController {

    private final BreedsMigrator breedsMigrator;
    private final MedicinesMigrator medicinesMigrator;


    public MigratorController(BreedsMigrator breedsMigrator, MedicinesMigrator medicinesMigrator) {
        this.breedsMigrator = breedsMigrator;
        this.medicinesMigrator = medicinesMigrator;
    }


    @PostMapping(value = "/breeds", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> migrateBreeds(@RequestPart(required = true) MultipartFile file) throws IOException {
        breedsMigrator.migrate(file);

        return ResponseEntity.status(200).build();
    }

    @PostMapping(value = "/medicines", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> migrateMedicines(@RequestPart(required = true) MultipartFile file) throws IOException, CsvException {
        medicinesMigrator.migrateMedicines(file);

        return ResponseEntity.status(200).build();
    }
}