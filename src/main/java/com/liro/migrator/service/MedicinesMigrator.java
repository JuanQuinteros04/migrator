package com.liro.migrator.service;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.liro.migrator.dtos.MedicineDTO;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Component
public class MedicinesMigrator {

    private static final CsvMapper mapper = new CsvMapper();

    public static <T> List<T> readCsvFile(MultipartFile file, Class<T> clazz) throws IOException {
        InputStream inputStream = file.getInputStream();
        CsvSchema schema = mapper.schemaFor(clazz).withHeader().withColumnReordering(true);
        ObjectReader reader = mapper.readerFor(clazz).with(schema);
        return reader.<T>readValues(inputStream).readAll();
    }

    public void migrateMedicines(MultipartFile file) throws IOException {

        List<MedicineDTO> medicineDTOs = readCsvFile(file, MedicineDTO.class);

        medicineDTOs.forEach(System.out::println);
    }
}
