package com.liro.migrator.service;


import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import com.liro.migrator.config.FeignUserClient;
import com.liro.migrator.dtos.AddressDTO;
import com.liro.migrator.dtos.ClientRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class ClientsMigrator {


    @Autowired
    FeignUserClient feignUserClient;

    public Void migrate(Long vetUserId, MultipartFile file) throws IOException {

        List<ClientRegister> clientRegisterList = new ArrayList<>();


        InputStream dbf = new ByteArrayInputStream(file.getBytes());;

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
                String ultVez = row.getString("Ultimavez");


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

        feignUserClient.createClients(clientRegisterList, vetUserId);

        clientRegisterList.forEach(System.out::println);

        return null;
    }
}