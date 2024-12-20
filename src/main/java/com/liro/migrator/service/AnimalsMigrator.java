package com.liro.migrator.service;

import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import com.liro.migrator.config.FeignAnimalClient;
import com.liro.migrator.config.FeignUserClient;
import com.liro.migrator.dtos.*;
import com.liro.migrator.dtos.enums.Sex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Component
public class AnimalsMigrator {
    @Autowired
    FeignAnimalClient feignAnimalClient;

    public List<AnimalMigrationResponse> migrate(Long vetClinicId, Long vetUserId, byte[] file, List<UserResponse> userResponses) throws IOException {

        List<AnimalDTO> animalDTOS = new ArrayList<>();


        InputStream dbf = new ByteArrayInputStream(file);

        try (DBFReader reader = new DBFReader(dbf)) {

            DBFRow row = reader.nextRow();

            while (row != null) {

                String codigoPaciente = row.getString("Codigopaci");
                String codigo = row.getString("Codigo");


                String nombre = row.getString("Nombre");
                String especie = row.getString("Especie");
                String raza = row.getString("Raza");
                String sexo = row.getString("Sexo");
                Date fechaNaci = row.getDate("Fecha_nac");
                Boolean vive = row.getBoolean("Vive");


                //Podría usarse para filtrar que usuarios migrar
                Date ultimaVez = row.getDate("Ultima_ves");
                Date fechaalta = row.getDate("Fechaalta");


                Optional<UserResponse> user  = userResponses.stream()
                        .filter(userResponse -> userResponse.getCodigoVetter().equals(vetClinicId + "-" + codigo))
                        .findFirst();


                if(user.isPresent() && vive){

                    AnimalDTO animalDTO = AnimalDTO.builder()
                            .name(nombre)
                            .surname(user.get().getSurname())
                            .death(false)
                            .vetterCode(codigoPaciente)
                            .sex(sexConverter(sexo))
                            .birthDate(fechaNaci)
                            .breed(raza)
                            .ownerUserId(user.get().getId())
                            .build();

                    animalDTOS.add(animalDTO);

                }

                row = reader.nextRow();

            }
        }

        return feignAnimalClient.createAnimals(animalDTOS, vetClinicId).getBody();
    }


    private Boolean isDeathConverter(String vive){
        return vive.equals("T") ? Boolean.FALSE : Boolean.TRUE;
    }

    private Sex sexConverter(String sexo){
        return sexo.equals("M") ? Sex.MALE : Sex.FEMALE;
    }

}