package com.liro.migrator.service;

import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import com.liro.migrator.config.FeignConsultationsClient;
import com.liro.migrator.dtos.AnimalMigrationResponse;
import com.liro.migrator.dtos.ConsultationDTO;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class ClinicaMigrator {
    @Autowired
    FeignConsultationsClient feignConsultationsClient;

    public Void migrate(Long vetUserId, byte[] clinicaDbf, byte[] clinicaFtp, List<AnimalMigrationResponse> animalResponses) throws IOException {

        List<ConsultationDTO> consultationDTOS = new ArrayList<>();

        animalResponses.forEach(animalResponse -> {

            InputStream dbf = new ByteArrayInputStream(clinicaDbf);


            try (DBFReader reader = new DBFReader(dbf)) {

                FileUtils.writeByteArrayToFile(new File("FTP." + vetUserId), clinicaFtp);

                reader.setMemoFile((new File("FTP." + vetUserId)));
                DBFRow row = reader.nextRow();

                while (row != null) {
                    String codigoPaciente = row.getString("Codigopaci");

                    AnimalMigrationResponse animal = animalResponses.stream()
                            .filter(animalMigrationResponse -> animalMigrationResponse.getVetterCode().equals(vetUserId + "-" + codigoPaciente))
                            .findFirst()
                            .orElseThrow(NoClassDefFoundError::new);

                    parseMedicalRecords(row.getString("DESCRIP"), consultationDTOS, animal);

                    row = reader.nextRow();
                }
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
            feignConsultationsClient.createConsultations(consultationDTOS, vetUserId);
        });

        return null;
    }

    private void parseMedicalRecords(String input, List<ConsultationDTO> consultationDTOS, AnimalMigrationResponse animalMigrationResponse) throws ParseException {


        // Separar las secciones del texto usando "******"
        String[] sections = input.split("\\*{6}");
        for (String section : sections) {
            if (section.trim().isEmpty()) {
                continue; // Ignorar secciones vacías
            }

            // Extraer la fecha
            String fechaRegex = "Fecha: (\\d{2}/\\d{2}/\\d{4})";
            Matcher fechaMatcher = Pattern.compile(fechaRegex).matcher(section);
            String fecha = fechaMatcher.find() ? fechaMatcher.group(1) : null;
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Extraer el atendido por
            String atendidoPorRegex = "Atendido Por: ([^\\n]+)";
            Matcher atendidoPorMatcher = Pattern.compile(atendidoPorRegex).matcher(section);
            String atendidoPor = atendidoPorMatcher.find() ? atendidoPorMatcher.group(1).trim() : "No encontrado";

            // Extraer el peso si existe
            String pesoRegex = "(?:Peso|peso): ([\\d,\\.\\s]+kg)";
            Matcher pesoMatcher = Pattern.compile(pesoRegex, Pattern.CASE_INSENSITIVE).matcher(section);
            String peso = pesoMatcher.find() ? pesoMatcher.group(1).trim() : "No encontrado";

            // El resto del texto se considera la descripción
            String descriptionRegex = "(?s)Fecha: \\d{2}/\\d{2}/\\d{4}.*?Atendido Por: [^\\n]+\\n(.*)";
            Matcher descriptionMatcher = Pattern.compile(descriptionRegex).matcher(section);
            String description = descriptionMatcher.find() ? descriptionMatcher.group(1).trim() : "No encontrada";


            ConsultationDTO consultationDTO = ConsultationDTO.builder()
                    .animalId(animalMigrationResponse.getId())
                    .weight(Double.valueOf(peso))
                    .details(description)
                    .build();

            consultationDTO.setLocalDate(fecha!=null? LocalDate.parse(fecha, formatoFecha) : null);

            consultationDTOS.add(consultationDTO);
        }
    }
}
