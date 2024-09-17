package com.liro.migrator.controller;

import com.liro.migrator.dtos.MigratorRequest;
import com.liro.migrator.service.BreedsMigrator;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

        public MigratorController(Queue<MigratorRequest> migratorQueue) {
            this.migratorQueue = migratorQueue;
        }

        @PostMapping(value = "/users", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<Void> migrateUsers(@RequestPart(required = true, value = "zipFile") MultipartFile zipFile,
                                                 @RequestParam("vetUserId") Long vetUserId) throws IOException {

            // Descomprimir el archivo ZIP
            byte[] usersFile = null;
            byte[] animalsFile = null;
            byte[] clinicaDbfFile = null;
            byte[] clinicaFtpFile = null;

            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipFile.getBytes()))) {
                ZipEntry zipEntry;

                while ((zipEntry = zis.getNextEntry()) != null) {
                    String fileName = zipEntry.getName();

                    // Lee el contenido del archivo ZIP
                    byte[] fileContent = IOUtils.toByteArray(zis);

                    // Verifica el nombre del archivo para saber qué archivo es
                    if (fileName.equalsIgnoreCase("clientes.dbf")) {
                        usersFile = fileContent;
                    } else if (fileName.equalsIgnoreCase("pacientes.dbf")) {
                        animalsFile = fileContent;
                    } else if (fileName.equalsIgnoreCase("clinica.dbf")) {
                        clinicaDbfFile = fileContent;
                    } else if (fileName.equalsIgnoreCase("clinica.ftp")) {
                        clinicaFtpFile = fileContent;
                    }

                    zis.closeEntry();
                }
            }

            // Verifica si los archivos requeridos están presentes
            if (usersFile == null || animalsFile == null || clinicaDbfFile == null || clinicaFtpFile == null) {
                return ResponseEntity.badRequest().build();
            }

            // Agrega la solicitud de migración a la cola
            migratorQueue.add(MigratorRequest.builder()
                    .animalsFile(animalsFile)
                    .usersFile(usersFile)
                    .clinicaFile(clinicaDbfFile)
                    .clinicaFileFTP(clinicaFtpFile)
                    .vetUserId(vetUserId)
                    .build());

            return ResponseEntity.status(200).build();
        }


    @PostMapping(value = "/breeds", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> migrateBreeds(@RequestPart(required = true) MultipartFile file) throws IOException {
        breedsMigrator.migrate(file);

        return ResponseEntity.status(200).build();
    }
}