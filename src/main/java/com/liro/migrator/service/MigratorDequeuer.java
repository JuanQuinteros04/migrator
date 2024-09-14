package com.liro.migrator.service;

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
    private final Queue<MigratorRequest> migratorQueue;

    @Autowired
    public MigratorDequeuer(ClientsMigrator clientsMigrator, AnimalsMigrator animalsMigrator, Queue<MigratorRequest> migratorQueue) {
        this.clientsMigrator = clientsMigrator;
        this.animalsMigrator = animalsMigrator;
        this.migratorQueue = migratorQueue;
    }

    @Scheduled(fixedDelay = 10000) // 10 segundos
    public void dequeue() {
        while (!migratorQueue.isEmpty()) {
            MigratorRequest migratorRequest = migratorQueue.poll();

            List<UserResponse> users;
            try {
                users = clientsMigrator.migrate(migratorRequest.getVetUserId(), migratorRequest.getUsersFile());
                animalsMigrator.migrate(migratorRequest.getVetUserId(), migratorRequest.getAnimalsFile(), users);
            } catch (IOException e) {
                // Manejar la excepción, pero no detener el scheduler
                e.printStackTrace();
            }
        }
    }
}