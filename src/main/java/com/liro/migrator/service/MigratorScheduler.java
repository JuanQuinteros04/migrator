package com.liro.migrator.service;

import com.liro.migrator.dtos.AnimalMigrationResponse;
import com.liro.migrator.dtos.MigratorRequest;
import com.liro.migrator.dtos.UserResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class MigratorScheduler {

    private static final String DIRECTORY_PATH = "/liro/vetter/migrate";
    private static final String PROCESSED_FOLDER_PATH = "/liro/vetter/processed";

    private final Set<String> processedFiles;
    private final ClientsMigrator clientsMigrator;
    private final AnimalsMigrator animalsMigrator;
    private final ClinicaMigrator clinicaMigrator;

    public MigratorScheduler( Set<String> processedFiles, ClientsMigrator clientsMigrator, AnimalsMigrator animalsMigrator, ClinicaMigrator clinicaMigrator) {
        this.clientsMigrator = clientsMigrator;
        this.animalsMigrator = animalsMigrator;
        this.clinicaMigrator = clinicaMigrator;
        this.processedFiles = processedFiles;
    }

    @Scheduled(fixedRate = 60000)
    public void processFiles() throws IOException {
        File folder = new File(DIRECTORY_PATH);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".zip"));

        if (files == null) {
            System.out.println("Not files found");
            return;
        }


        for (File file : files) {
            if (processedFiles.contains(file.getName())) {
                continue;
            }
            System.out.printf("File found in: " + file.getName());

            Long vetUserId = extractVetUserIdFromFileName(file.getName());

            // Procesar archivo ZIP
            processZipFile(file, vetUserId);

            // Mover archivo a carpeta de procesados
            moveFileToProcessedFolder(file);
            // Registrar archivo como procesado

            processedFiles.add(file.getName());
        }
    }

    private void processZipFile(File file, Long vetUserId) throws IOException {
        byte[] usersFile = null;
        byte[] animalsFile = null;
        byte[] clinicaDbfFile = null;
        byte[] clinicaFtpFile = null;


        List<UserResponse> users;
        List<AnimalMigrationResponse> animals;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry zipEntry;

            while ((zipEntry = zis.getNextEntry()) != null) {
                System.out.printf("Ingreso a leer");

                String fileName = zipEntry.getName();
                byte[] fileContent = IOUtils.toByteArray(zis);
                System.out.println(fileName);
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

        if (usersFile != null && animalsFile != null && clinicaDbfFile != null && clinicaFtpFile != null) {
            System.out.printf("Files found");

            try {
                users = clientsMigrator.migrate(vetUserId, usersFile);
                animals = animalsMigrator.migrate(vetUserId, animalsFile, users);
                clinicaMigrator.migrate(vetUserId, clinicaDbfFile, clinicaFtpFile, animals);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("File not found: ");
            if(usersFile == null){
                System.out.println("usersFile");
            }
            if(animalsFile == null){
                System.out.println("animalsFile");
            }
            if(clinicaDbfFile == null){
                System.out.println("clinicaDbfFile");
            }
            if(clinicaFtpFile == null){
                System.out.println("clinicaFtpFile");
            }
        }
    }

    private void moveFileToProcessedFolder(File file) throws IOException {
        Path processedDirectory = Paths.get(PROCESSED_FOLDER_PATH);
        if (!Files.exists(processedDirectory)) {
            Files.createDirectories(processedDirectory);
        }
        Files.move(file.toPath(), processedDirectory.resolve(file.getName()));
    }

    private Long extractVetUserIdFromFileName(String fileName) {
        try {
            // Extraer el número antes de la extensión .zip
            String vetUserIdStr = fileName.replace(".zip", "");
            return Long.parseLong(vetUserIdStr);
        } catch (NumberFormatException e) {
            // Si no se puede convertir el nombre en un número, retorna null
            return null;
        }
    }
}