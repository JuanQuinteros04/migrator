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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


@Component
public class ClinicaMigrator {
    @Autowired
    FeignConsultationsClient feignConsultationsClient;
    private static final Pattern FECHA_PATTERN = Pattern.compile("Fecha: (\\d{2}/\\d{2}/\\d{4})");
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("(?s)Fecha: \\d{2}/\\d{2}/\\d{4}.*?Atendido Por: [^\\n]+\\n(.*)");

    public Void migrate(Long vetClinicId, Long vetUserId, byte[] clinicaDbf, File clinicaFtp, List<AnimalMigrationResponse> animalResponses) throws IOException {

        List<ConsultationDTO> consultationDTOS = new ArrayList<>();


            InputStream dbf = new ByteArrayInputStream(clinicaDbf);


            try (DBFReader reader = new DBFReader(dbf)) {


                reader.setMemoFile(clinicaFtp);

                DBFRow row = reader.nextRow();

                int i = 0;
                while (row != null) {
                    String codigoPaciente = row.getString("Codigopaci");

                    Optional<AnimalMigrationResponse> animal = animalResponses.stream()
                            .filter(animalMigrationResponse -> animalMigrationResponse.getVetterCode().equals(vetClinicId + "-" + codigoPaciente))
                            .findFirst();

                    if(animal.isPresent()){
                        parseMedicalRecords(row.getString("DESCRIP"), consultationDTOS, animal.get());

                    }

                    row = reader.nextRow();
                }
            } catch ( ParseException e) {
                throw new RuntimeException(e);
            }

            feignConsultationsClient.createConsultations(consultationDTOS, vetClinicId, vetUserId);

        return null;
    }

    private void parseMedicalRecords(String input, List<ConsultationDTO> consultationDTOS, AnimalMigrationResponse animalMigrationResponse) throws ParseException {

        String[] sections = input.split("\\*{6}");
        Stream.of(sections).parallel().forEach(section -> {

            // Extraer la fecha
            //String fechaRegex = "Fecha: (\\d{2}/\\d{2}/\\d{4})";
            Matcher fechaMatcher = FECHA_PATTERN.matcher(section);
            String fecha = fechaMatcher.find() ? fechaMatcher.group(1) : null;
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

           /* // Extraer el atendido por
            String atendidoPorRegex = "Atendido Por: ([^\\n]+)";
            Matcher atendidoPorMatcher = Pattern.compile(atendidoPorRegex).matcher(section);
            String atendidoPor = atendidoPorMatcher.find() ? atendidoPorMatcher.group(1).trim() : "No encontrado";

            // Extraer el peso si existe
            String pesoRegex = "(?:Peso|peso): ([\\d,\\.\\s]+kg)";
            Matcher pesoMatcher = Pattern.compile(pesoRegex, Pattern.CASE_INSENSITIVE).matcher(section);
            String peso = pesoMatcher.find() ? pesoMatcher.group(1).replaceAll("\\s+", "").trim() : null;*/

            // El resto del texto se considera la descripción
            //String descriptionRegex = "(?s)Fecha: \\d{2}/\\d{2}/\\d{4}.*?Atendido Por: [^\\n]+\\n(.*)";
            Matcher descriptionMatcher = DESCRIPTION_PATTERN.matcher(section);
            String description = descriptionMatcher.find() ? descriptionMatcher.group(1).trim() : null;


            ConsultationDTO consultationDTO = ConsultationDTO.builder()
                    .animalId(animalMigrationResponse.getId())
                    .details(description)
                    .build();

            consultationDTO.setLocalDate(fecha!=null? LocalDate.parse(fecha, formatoFecha) : null);

/*
            if(peso!=null){
                try {
                    consultationDTO.setWeight(Double.valueOf(peso));
                }catch (Exception ex){
                    System.out.printf("Peso invalido: " + peso);
                }
            }

 */
            consultationDTOS.add(consultationDTO);

        });
    }
}