package com.liro.migrator.service;

import com.liro.migrator.config.FeignAnimalClient;
import com.liro.migrator.dtos.BreedDTO;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class BreedsMigrator
{
    @Autowired
    FeignAnimalClient feignAnimalClient;

    public Void migrate(MultipartFile file, String  animalType) throws IOException {


        Map<String, Integer> fields = new HashMap<>();
        List<BreedDTO> breedDTOS = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {


            Iterator<String[]> iterator = reader.iterator();

            String[] firstLine = iterator.next();

            for (int i = 0; i < firstLine.length; i++) {
                fields.put(firstLine[i], i);
            }

            while (iterator.hasNext()) {
                iterator.next();
                String[] row = iterator.next();



                breedDTOS.add(BreedDTO.builder()
                        .name(row[fields.get("Raza")])
                        .formalName(row[fields.get("Raza")])
                        .animalTypeId(typeConverter(animalType))
                        .alternativeNames(setsConverter(row[fields.get("Otros nombres")]))
                        .build());
            }

            feignAnimalClient.createBreeds(breedDTOS);

            return null;
        }
    }

    private Long typeConverter(String especie) {
        if(especie.equals("CANINO")){
            return 2L;
        }
        else if(especie.equals("FELINO")) {
            return 3L;
        } else return null;
    }

    private Set<String> setsConverter(String setInput) {

        Set<String> sets = new HashSet<>();

        String[] setsSplit = setInput.split(";");

        for(int i = 0; i < setsSplit.length; i++){
            if(!StringUtils.hasText((setsSplit[i]))) {
                System.out.println(setsSplit[i].toLowerCase().trim());
                sets.add(setsSplit[i].toLowerCase().trim());
            }
        }
        return sets;
    }
}