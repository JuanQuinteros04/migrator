package com.liro.migrator.service;

import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import com.liro.migrator.config.FeignAnimalClient;
import com.liro.migrator.dtos.AnimalDTO;
import com.liro.migrator.dtos.BreedDTO;
import com.liro.migrator.dtos.enums.Sex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class BreedsMigrator {
    @Autowired
    FeignAnimalClient feignAnimalClient;

    public Void migrate(MultipartFile file) throws IOException {

        List<BreedDTO> breedDTOS = new ArrayList<>();


        InputStream dbf = new ByteArrayInputStream(file.getBytes());;

        try (DBFReader reader = new DBFReader(dbf)) {

            DBFRow row = reader.nextRow();

            while (row != null) {

                String nombre = row.getString("Raza");
                String especie = row.getString("Especie");


                if(typeConverter(especie)!=null){

                    BreedDTO breedDTO = BreedDTO.builder()
                            .name(nombre)
                            .formalName(nombre)
                            .animalTypeId(typeConverter(especie))
                            .build();

                    breedDTOS.add(breedDTO);
                }

                row = reader.nextRow();
            }
        }

        feignAnimalClient.createBreeds(breedDTOS);

        return null;
    }

    private Long typeConverter(String especie) {
        if(especie.equals("CANINO")){
            return 2L;
        }
        else if(especie.equals("FELINO")) {
            return 3L;
        } else return null;
    }
}