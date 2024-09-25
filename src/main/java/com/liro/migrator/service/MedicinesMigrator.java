package com.liro.migrator.service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class MedicinesMigrator {

    private static final CsvMapper mapper = new CsvMapper();


    public void migrateMedicines(MultipartFile file) throws IOException, CsvException {

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = reader.readAll();


            records.forEach(record -> {

                System.out.println(record[0] + ", " + record[1]);

            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
