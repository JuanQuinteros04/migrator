package com.liro.migrator.service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.liro.migrator.config.FeignAnimalClient;
import com.liro.migrator.config.FeignMedicineClient;
import com.liro.migrator.dtos.MedicineDTO;
import com.liro.migrator.dtos.enums.AnimalType;
import com.liro.migrator.dtos.enums.Sex;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class MedicinesMigrator {

    @Autowired
    FeignMedicineClient feignMedicineClient;
    private static final CsvMapper mapper = new CsvMapper();

    public void migrateMedicines(MultipartFile file) throws IOException, CsvException {


        Map<String, Integer> fields = new HashMap<>();
        List<MedicineDTO> medicineDTOS = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {


            Iterator<String[]> iterator = reader.iterator();

            String[] firstLine = iterator.next();

            for (int i = 0; i < firstLine.length; i++) {
                fields.put(firstLine[i], i);
            }

            while (iterator.hasNext()) {
                String[] row = iterator.next();

                medicineDTOS.add(MedicineDTO.builder()
                        .brandName(row[fields.get("Marca")])
                        .medicineType(row[fields.get("Tipo")])
                        .presentationName(row[fields.get("Presentación")])
                        .animalType(animalTypeConverter(row[fields.get("Uso en")]))
                        .dosesUnity(row[fields.get("Unidad Dosis")])
                        .commercialName(row[fields.get("Nombre comercial")])
                        .formalName(row[fields.get("Nombre científico")])
                        .onlyVetUse(onlyVetUseConverter(row[fields.get("Uso")]))
                        .components(setsConverter(row[fields.get("Combate")]))
                        .medicineGroups(setsConverter(row[fields.get("SubTipo")]))
                        .needPrescription(needPrescriptionConverter(row[fields.get("Tipo de venta")]))
                        .build());

            }

            feignMedicineClient.createMedicines(medicineDTOS);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


   //need to be changed when adding one to many relation in medicines (not enum).
    private AnimalType animalTypeConverter(String animalType) {

        String[] animalTypesSplit = animalType.split(",");

        if (animalTypesSplit.length > 1) {
            return AnimalType.ALL_TYPE;
        } else {
            switch (animalTypesSplit[0]) {

                case "Felinos":
                    return AnimalType.CAT;
                case "Caninos":
                    return AnimalType.DOG;
                default: return AnimalType.ALL_TYPE;
            }
        }
    }

    private Boolean needPrescriptionConverter(String needPrescription) {

        return needPrescription.equals("Bajo receta") ? Boolean.TRUE : Boolean.FALSE;
    }

    private Boolean onlyVetUseConverter(String onlyVetUse) {

        return onlyVetUse.equals("Uso veterinario") ? Boolean.TRUE : Boolean.FALSE;
    }

    private Set<String> setsConverter(String setInput) {

        Set<String> sets = new HashSet<>();

        String[] setsSplit = setInput.split(",");

        for(int i = 0; i < setsSplit.length; i++){
            if(StringUtils.hasText((setsSplit[i]))) {
                sets.add(setsSplit[i]);
            }
        }
        return sets;
    }
}
