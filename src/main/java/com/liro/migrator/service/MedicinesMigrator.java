package com.liro.migrator.service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.liro.migrator.dtos.MedicineDTO;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class MedicinesMigrator {

    private static final CsvMapper mapper = new CsvMapper();


    public void migrateMedicines(MultipartFile file) throws IOException, CsvException {


        Map<String, Integer> fields = new HashMap<>();
        List<MedicineDTO> medicineDTOS = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {


            Iterator<String[]> iterator = reader.iterator();

            String[] firstLine  = iterator.next();

            for(int i = 0; i < firstLine.length; i++){
                fields.put(firstLine[i], i);
            }

            while(iterator.hasNext()){
                String[] row = iterator.next();

                medicineDTOS.add(MedicineDTO.builder()
                        .brandName(row[fields.get("Marca")])
                        .build());

            }

            medicineDTOS.forEach(System.out::println);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
