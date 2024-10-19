package com.liro.migrator.service;

import com.liro.migrator.config.FeignMedicineClient;
import com.liro.migrator.dtos.AnimalMigrationResponse;
import com.liro.migrator.dtos.ApplicationRecordDTO;
import com.liro.migrator.dtos.MedicineDTO;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ApplicationsMigrator {

    @Autowired
    FeignMedicineClient feignMedicineClient;
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public void migrateApplications(MultipartFile file, Long vetUserId, List<AnimalMigrationResponse> animalMigrationResponses) throws IOException, CsvException {

        List<ApplicationRecordDTO> applicationRecordDTOS = new ArrayList<>();

        Map<String, Integer> fields = new HashMap<>();


        // Leer el archivo CSV (vacunas)
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {


            Iterator<String[]> iterator = reader.iterator();

            String[] firstLine = iterator.next();

            for (int i = 0; i < firstLine.length; i++) {
                fields.put(firstLine[i], i);

                while (iterator.hasNext()) {
                    String[] row = iterator.next();

                    String codigoPaciente = row[fields.get("Codigopaci")];


                    Optional<AnimalMigrationResponse> animal = animalMigrationResponses.stream()
                            .filter(animalMigrationResponse -> animalMigrationResponse.getVetterCode().equals(vetUserId + "-" + codigoPaciente))
                            .findFirst();


                    ApplicationRecordDTO applicationDTO = ApplicationRecordDTO.builder()
                          //  .animalId(row[fields.get(animal)])// De donde sale? se supone que ya esta en la DB
                            .medicineId(medicineIdConverter(row[fields.get("vacuna")]))
                           // .applicationDate(dateConverter(row[fields.get("fecha_apli")]))
                           // .endDate(dateConverter(row[fields.get("fecha_reva")]))
                            .build();

                    applicationRecordDTOS.add(applicationDTO);
                }

                feignMedicineClient.createApplications(applicationRecordDTOS);
            }
        }
    }



    private Long medicineIdConverter(String name){
        switch (name){
            case "CUADRUPLE":
                return 960L;

            case "DHPPI":
                return 123L;

            case "LEPTOSPIRA":
                return 959L;

            case "MOQUILLO":
                return 958L;

            case "OCTUVALENTE":
                return 957L;

            case "PARVOVIRUS":
                return 956L;

            case "PUPPY":
                return 955L;

            case "ATP EXTERNO":
                return 961L;

            case "ATP INYECTABLE":
                return 962L;

            case "ATP COMPRIMIDOS":
                return 963L;

            default:
                return null;
        }
    }

    private String dateConverter(String date){

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // Formato de entrada
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Formato de salida

        LocalDate applicationDate = LocalDate.parse(date, inputFormatter);

        return applicationDate.format(outputFormatter);
    }
}
