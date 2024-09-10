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

    @Autowired
    private final AnimalsMigrator animalsMigrator;

    @Autowired
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
                try {
                    // Este método bloquea hasta que haya un elemento disponible en la cola
                    MigratorRequest migratorRequest = migratorQueue.poll();

                    List<UserResponse> users = clientsMigrator.migrate(migratorRequest.getVetUserId(), migratorRequest.getUsersFile());
                    animalsMigrator.migrate(migratorRequest.getVetUserId(), migratorRequest.getAnimalsFile(), users);

                } catch (IOException e) {
                    e.printStackTrace();  // Manejo de la excepción
                }
            }
        });
        backgroundThread.start();
    }
}
