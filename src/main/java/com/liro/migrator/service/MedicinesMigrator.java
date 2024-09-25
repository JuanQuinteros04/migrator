package com.liro.migrator.service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.liro.migrator.dtos.MedicineDTO;
import com.liro.migrator.dtos.enums.AnimalType;
import com.liro.migrator.dtos.enums.Sex;
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
    private static final List<String> polivalentes = Arrays.asList("Quíntuple", "Cuádruple", "Triple", "Doble");

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
                        .medicineTypeId(medicineTypeConverter(row[fields.get("Tipo")]))
                        .presentationName(row[fields.get("Presentación")])
                        .animalType(animalTypeConverter(row[fields.get("Uso en")]))
                        .dosesUnity(row[fields.get("Unidad Dosis")])
                        .commercialName(row[fields.get("Nombre comercial")])
                        .formalName(row[fields.get("Nombre científico")])
                        .onlyVetUse(onlyVetUseConverter(row[fields.get("Uso")]))
                        .medicineGroups(medicinesGroupsConverter(row[fields.get("SubTipo")]))
                        .needPrescription(needPrescriptionConverter(row[fields.get("Tipo de venta")]))
                        .build());

            }

            medicineDTOS.forEach(System.out::println);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Long medicineTypeConverter(String medicineType) {

        switch (medicineType) {

            case "Vacuna":
                return 1L;
            case "Antiparasitario Interno":
                return 2L;
            case "Antiparasitario Externo":
            default:
                return 3L;
        }
    }

    private AnimalType animalTypeConverter(String animalType) {

        switch (animalType) {

            case "Felinos":
                return AnimalType.CAT;
            case "Caninos":
                return AnimalType.DOG;
            case "Caninos y Felinos":
            default:
                return AnimalType.ALL_TYPE;
        }
    }

    private Boolean needPrescriptionConverter(String needPrescription) {

        return needPrescription.equals("Bajo receta") ? Boolean.TRUE : Boolean.FALSE;
    }

    private Boolean onlyVetUseConverter(String onlyVetUse) {

        return onlyVetUse.equals("Uso Veterinario") ? Boolean.TRUE : Boolean.FALSE;
    }

    private List<String> medicinesGroupsConverter(String medicineGroup) {

        List<String> medicinesGroups = new ArrayList<>();
        medicinesGroups.add(medicineGroup);

        polivalentes.forEach(polivalente -> {
            if (polivalente.contains(medicineGroup)) {
                medicinesGroups.add("polivalent");

            }
        });

        return medicinesGroups;
    }
}
