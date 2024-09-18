package com.liro.migrator.service;


import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import com.liro.migrator.config.FeignUserClient;
import com.liro.migrator.dtos.AddressDTO;
import com.liro.migrator.dtos.ClientRegister;
import com.liro.migrator.dtos.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ClientsMigrator {


    @Autowired
    FeignUserClient feignUserClient;

    public List<UserResponse> migrate(Long vetUserId, byte[] file) throws IOException {

        List<ClientRegister> clientRegisterList = new ArrayList<>();


        InputStream dbf = new ByteArrayInputStream(file);

        try (DBFReader reader = new DBFReader(dbf)) {

            DBFRow row = reader.nextRow();

            while (row != null) {

                String nombre = row.getString("Nombre");

                String localidad = row.getString("Localidad");
                String direccion = row.getString("Direccion");
                String provincia = row.getString("Provincia");
                String codPostal = row.getString("Codpostal");


                String telefono = row.getString("Telefono");


                String email = row.getString("Email");
                String saldo = row.getString("Saldo");
                String codigo = row.getString("Codigo");


                //Podría usarse para filtrar que usuarios migrar
                Date ultVez = row.getDate("Ultima_ves");



                if (ultVez != null) {
                    // Convertir Date a LocalDate
                    LocalDate fechaUltimaVez = ultVez.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate fechaActual = LocalDate.now();

                    // Calcular la diferencia en años
                    long diferenciaEnAnios = ChronoUnit.YEARS.between(fechaUltimaVez, fechaActual);

                    if (diferenciaEnAnios < 2) {
                        AddressDTO addressDTO = AddressDTO.builder()
                                .city(provincia)
                                .country("Argentina")
                                .location(localidad)
                                .addressLine1(direccion)
                                .postalCode(codPostal)
                                .build();

                        ClientRegister clientRegister = ClientRegister.builder()
                                .name(nombre)
                                .phoneNumber(telefono)
                                .saldo(Double.valueOf(saldo))
                                .email(email)
                                .address(addressDTO)
                                .codigo(codigo)
                                .build();

                        clientRegisterList.add(clientRegister);

                        row = reader.nextRow();
                    }
                }

            }
        }

        return feignUserClient.createClients(clientRegisterList, vetUserId).getBody();

    }
}