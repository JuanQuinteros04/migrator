package com.liro.migrator.service;

import com.liro.migrator.dtos.MigratorRequest;
import com.liro.migrator.dtos.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Queue;

@Component
public class MigratorDequeuer {

    @Autowired
    private final ClientsMigrator clientsMigrator;


    private final AnimalsMigrator animalsMigrator;

    private final Queue<MigratorRequest> migratorQueue;

    public MigratorDequeuer(ClientsMigrator clientsMigrator, AnimalsMigrator animalsMigrator, Queue<MigratorRequest> migratorQueue) {
        this.clientsMigrator = clientsMigrator;
        this.animalsMigrator = animalsMigrator;
        this.migratorQueue = migratorQueue;
    }

    @PostConstruct
    public void dequeue() throws IOException {

        Thread backgroundThread = new Thread(() -> {
            while (true) {

                if (!migratorQueue.isEmpty()) {
                    MigratorRequest migratorRequest = migratorQueue.poll();

                    List<UserResponse> users = null;
                    try {
                        users = clientsMigrator.migrate(migratorRequest.getVetUserId(), migratorRequest.getUsersFile());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        animalsMigrator.migrate(migratorRequest.getVetUserId(), migratorRequest.getAnimalsFile(), users);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });
        backgroundThread.start();
    }
}
