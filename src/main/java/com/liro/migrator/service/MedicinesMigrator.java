package com.liro.migrator.service;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.liro.migrator.dtos.MedicineDTO;
import com.liro.migrator.dtos.enums.AnimalType;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


        Reader reader = new InputStreamReader(file.getInputStream());

        CsvToBean<MedicineDTO> csvToBean = new CsvToBean<MedicineDTO>();

        Map<String, String> columnMapping = new HashMap<String, String>();
        columnMapping.put("Nombre Comercial", "commercialName");
        columnMapping.put("Nombre científico", "formalName");
        columnMapping.put("Uso", "onlyVetUse");
        columnMapping.put("Tipo de venta", "needPrescription");
        columnMapping.put("Conservación", "conservation");
        columnMapping.put("Contenido", "content");
        columnMapping.put("Unidad Dosis", "dosesUnity");
        columnMapping.put("Marca", "brandName");
        columnMapping.put("Uso en", "animalType");
        columnMapping.put("Tipo", "medicineTypeId");
        columnMapping.put("SubTipo", "medicineGroups");
        columnMapping.put("Presentación", "presentationName");




        HeaderColumnNameTranslateMappingStrategy<MedicineDTO> strategy = new HeaderColumnNameTranslateMappingStrategy<MedicineDTO>();
        strategy.setType(MedicineDTO.class);
        strategy.setColumnMapping(columnMapping);

        List<MedicineDTO> list = null;
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        csvToBean.setCsvReader(csvReader);
        csvToBean.setMappingStrategy(strategy);
        list = csvToBean.parse();

        list.forEach(System.out::println);
    }
}
