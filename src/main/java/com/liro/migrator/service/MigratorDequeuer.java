package com.liro.migrator.service;

import com.liro.migrator.dtos.MigratorRequest;
import com.liro.migrator.dtos.UserResponse;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Queue;

@Component
public class MigratorDequeuer {

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

        while (true){

            if(!migratorQueue.isEmpty()){

                MigratorRequest migratorRequest = migratorQueue.poll();

                List<UserResponse> users =  clientsMigrator.migrate(migratorRequest.getVetUserId(), migratorRequest.getUsersFile());
                animalsMigrator.migrate(migratorRequest.getVetUserId(), migratorRequest.getAnimalsFile(), users);

            }
        }

    }
}
