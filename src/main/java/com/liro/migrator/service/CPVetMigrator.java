package com.liro.migrator.service;

import com.liro.migrator.dtos.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Objects;

@Component
public class CPVetMigrator {


    private static final String DIRECTORY_PATH = "/liro/cpvet/migrate";
    private static final String PROCESSED_FOLDER_PATH = "/liro/cpvet/processed";


    public CPVetMigrator() {
    }

    @Scheduled(fixedRate = 6000000)
    public void processFiles() throws IOException {
        System.out.println("ingreso cpvet");
        File folder = new File(DIRECTORY_PATH);
        File[] files = folder.listFiles();

        if (files == null) {
            System.out.println("Not files found");
            return;
        }

        System.out.println("encontro cpvet");

        for (File file : files) {

            System.out.printf("File found in: " + file.getName());

            Long credentials[] = extractVetUserIdsFromFileName(file.getName());


            processZipFile(file, credentials[0], credentials[1]);

            // Mover archivo a carpeta de procesados
            moveFileToProcessedFolder(file);
            // Registrar archivo como procesado
        }
    }

    private void processZipFile(File file, Long vetClinicId, Long vetUserId) throws IOException {
        System.out.println("procesando cpvet");


        List<UserResponse> users;
        List<AnimalMigrationResponse> animals;

            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;

            try {

                // Path to your .mdb file
                String databasePath = file.getAbsolutePath();

                // UCanAccess JDBC URL
                String url = "jdbc:ucanaccess://" + databasePath +
                        ";jackcessOpener=com.liro.migrator.config.CryptCodecOpener";

                // Establish the connection
                connection = DriverManager.getConnection(url);

                // Create a statement
                statement = connection.createStatement();

                // Execute a query to read data from the table
                resultSet = statement.executeQuery("SELECT * FROM Identificacion");

                ClientRegister current = null;
                // Process the result set
                while (resultSet.next()) {
                    String name = resultSet.getString("Propietario");
                    String direccion = resultSet.getString("Domicilio");
                    String tel = resultSet.getString("Tel");
                    String nameMascota = resultSet.getString("Mascotas");
                    String birthDate = resultSet.getString("BirthDate");
                    String sexo = resultSet.getString("Sexo");
                    String especie = resultSet.getString("Especie");
                    String raza = resultSet.getString("Raza");
                    String peso = resultSet.getString("Peso");
                    String id = resultSet.getString("IDPac");


                    if(current ==null || (!Objects.equals(current.getAddress().getAddressLine1(), direccion) || Objects.equals(current.getName(), name))){

                        AddressDTO addressDTO = AddressDTO.builder()
                                .city(null)
                                .country("Argentina")
                                .location(null)
                                .addressLine1(direccion)
                                .postalCode(null)
                                .build();

                        current = ClientRegister.builder()
                                .name(name)
                                .phoneNumber(tel)
                                .saldo(0.00)
                                .email(null)
                                .address(addressDTO)
                                .codigo(null)
                                .build();
                    }


/*
                    AnimalDTO animalDTO = AnimalDTO.builder()
                            .name(nameMascota)
                            .surname(null)
                            .death(false)
                            .vetterCode(codigoPaciente)
                            .sex(sexConverter(sexo))
                            .birthDate(fechaNaci)
                            .breed(raza)
                            .ownerUserId(user.get().getId())
                            .build();
*/
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close resources
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }



    private void moveFileToProcessedFolder(File file) throws IOException {
        Path processedDirectory = Paths.get(PROCESSED_FOLDER_PATH);
        if (!Files.exists(processedDirectory)) {
            Files.createDirectories(processedDirectory);
        }
        Files.move(file.toPath(), processedDirectory.resolve(file.getName()));
    }

    private Long[] extractVetUserIdsFromFileName(String fileName) {
        try {
            // Elimina la extensión .zip del nombre del archivo
            String vetUserIdsStr = fileName.replace(".MDB", "");

            // Divide el nombre usando el guion para extraer los dos números
            String[] parts = vetUserIdsStr.split("-");

            // Convierte los dos valores en Long
            Long vetClinicId = Long.parseLong(parts[0]);
            Long vetUserId = Long.parseLong(parts[1]);

            // Retorna un arreglo con los dos valores
            return new Long[]{vetClinicId, vetUserId};
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Si hay algún error al convertir o dividir, retorna null
            return null;
        }
    }
}

