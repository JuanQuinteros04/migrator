package com.liro.migrator.service;

import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import com.liro.migrator.config.FeignAnimalClient;
import com.liro.migrator.config.FeignUserClient;
import com.liro.migrator.dtos.AddressDTO;
import com.liro.migrator.dtos.AnimalDTO;
import com.liro.migrator.dtos.ClientRegister;
import com.liro.migrator.dtos.UserResponse;
import com.liro.migrator.dtos.enums.Sex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class AnimalsMigrator {
    @Autowired
    FeignAnimalClient feignAnimalClient;

    public Void migrate(Long vetUserId, byte[] file, List<UserResponse> userResponses) throws IOException {

        List<AnimalDTO> animalDTOS = new ArrayList<>();


        InputStream dbf = new ByteArrayInputStream(file);

        try (DBFReader reader = new DBFReader(dbf)) {

            DBFRow row = reader.nextRow();

            while (row != null) {

                String codigoPaciente = row.getString("Codigopaci");

                String nombre = row.getString("Nombre");
                String especie = row.getString("Especie");
                String raza = row.getString("Raza");
                String sexo = row.getString("Sexo");
                Date fechaNaci = row.getDate("Fecha_naci");
                String vive = row.getString("Vive");


                //Podría usarse para filtrar que usuarios migrar
                Date ultimaVez = row.getDate("Ultima_ves");
                Date fechaalta = row.getDate("Fechaalta");


                UserResponse user  = userResponses.stream()
                        .filter(userResponse -> userResponse.getCodigoVetter().equals(vetUserId + "-" + codigoPaciente))
                        .findFirst()
                        .orElseThrow(NoClassDefFoundError::new);

                AnimalDTO animalDTO = AnimalDTO.builder()
                        .name(nombre)
                        .surname(user.getSurname())
                        .death(isDeathConverter(vive))
                        .sex(sexConverter(sexo))
                        .birthDate(fechaNaci)
                        .breed(raza)
                        .ownerUserId(user.getId())
                        .build();

                animalDTOS.add(animalDTO);

                row = reader.nextRow();
            }
        }

        feignAnimalClient.createAnimals(animalDTOS, vetUserId);

        return null;
    }


    private Boolean isDeathConverter(String vive){
        return vive.equals("T") ? Boolean.FALSE : Boolean.TRUE;
    }

    private Sex sexConverter(String sexo){
        return sexo.equals("M") ? Sex.MALE : Sex.FEMALE;
    }

}