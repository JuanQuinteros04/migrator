package com.liro.migrator.service;

import com.liro.migrator.dtos.AnimalMigrationResponse;
import com.liro.migrator.dtos.MigratorRequest;
import com.liro.migrator.dtos.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Queue;

@Component
public class MigratorDequeuer {

    private final ClientsMigrator clientsMigrator;
    private final AnimalsMigrator animalsMigrator;
    private final ClinicaMigrator clinicaMigrator;

    private final Queue<MigratorRequest> migratorQueue;

    @Autowired
    public MigratorDequeuer(ClientsMigrator clientsMigrator, AnimalsMigrator animalsMigrator, ClinicaMigrator clinicaMigrator, Queue<MigratorRequest> migratorQueue) {
        this.clientsMigrator = clientsMigrator;
        this.animalsMigrator = animalsMigrator;
        this.clinicaMigrator = clinicaMigrator;
        this.migratorQueue = migratorQueue;
    }

    @Scheduled(fixedDelay = 10000) // 10 segundos
    public void dequeue() {
        while (!migratorQueue.isEmpty()) {
            MigratorRequest migratorRequest = migratorQueue.poll();

            List<UserResponse> users;
            List<AnimalMigrationResponse> animals;

            try {
                users = clientsMigrator.migrate(migratorRequest.getVetUserId(), migratorRequest.getUsersFile());
                animals = animalsMigrator.migrate(migratorRequest.getVetUserId(), migratorRequest.getAnimalsFile(), users);
                clinicaMigrator.migrate(migratorRequest.getVetUserId(), migratorRequest.getClinicaFile(), migratorRequest.getClinicaFileFTP(), animals);
            } catch (IOException e) {
                // Manejar la excepción, pero no detener el scheduler
                e.printStackTrace();
            }
        }
    }
}