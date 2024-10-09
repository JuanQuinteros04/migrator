package com.liro.migrator.service;

import com.liro.migrator.dtos.ApplicationRecordDTO;
import com.liro.migrator.dtos.MedicineDTO;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class ApplicationsMigrator {

    public void migrateApplications(MultipartFile file) throws IOException, CsvException {
        Map<String, Integer> fields = new HashMap<>();
        List<ApplicationRecordDTO> applicationRecordDTOS = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {


            Iterator<String[]> iterator = reader.iterator();

            String[] firstLine = iterator.next();

            for (int i = 0; i < firstLine.length; i++) {
                fields.put(firstLine[i], i);
            }

//            while (iterator.hasNext()) {
//                String[] row = iterator.next();
//
//                applicationRecordDTOS.add(MedicineDTO.builder()
//                        .brandName(row[fields.get("Marca")])
//                        .medicineTypeId(medicineTypeConverter(row[fields.get("Tipo")]))
//                        .presentationName(row[fields.get("Presentación")])
//                        .animalType(animalTypeConverter(row[fields.get("Uso en")]))
//                        .dosesUnity(row[fields.get("Unidad Dosis")])
//                        .commercialName(row[fields.get("Nombre comercial")])
//                        .formalName(row[fields.get("Nombre científico")])
//                        .onlyVetUse(onlyVetUseConverter(row[fields.get("Uso")]))
//                        .medicineGroups(medicinesGroupsConverter(row[fields.get("SubTipo")]))
//                        .needPrescription(needPrescriptionConverter(row[fields.get("Tipo de venta")]))
//                        .build());
//
//            }

//            feignMedicineClient.createMedicines(medicineDTOS);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
