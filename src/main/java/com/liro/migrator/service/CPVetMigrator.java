package com.liro.migrator.service;

import com.liro.migrator.config.FeignAnimalClient;
import com.liro.migrator.config.FeignConsultationsClient;
import com.liro.migrator.config.FeignUserClient;
import com.liro.migrator.dtos.*;
import com.liro.migrator.dtos.enums.Sex;
import org.apache.commons.lang.StringUtils;
import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

@Component
public class CPVetMigrator {


    private static final String DIRECTORY_PATH = "/liro/cpvet/migrate";
    private static final String PROCESSED_FOLDER_PATH = "/liro/cpvet/processed";

    @Autowired
    FeignUserClient feignUserClient;

    @Autowired
    FeignConsultationsClient feignConsultationsClient;
    @Autowired
    FeignAnimalClient feignAnimalClient;

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

            HashMap<String, ClientRegister> clientRegisterRequest = new HashMap<>();

            // Process the result set
            while (resultSet.next()) {
                String name = resultSet.getString("Propietario");
                String direccion = resultSet.getString("Domicilio");
                String tel = resultSet.getString("Tel");


                if (StringUtils.isNotBlank(tel)) {

                    AddressDTO addressDTO = AddressDTO.builder()
                            .city("Córdoba")
                            .country("Argentina")
                            .location(null)
                            .addressLine1(direccion)
                            .postalCode(null)
                            .build();

                    ClientRegister clientRegister = ClientRegister.builder()
                            .name(name)
                            .phoneNumber(tel)
                            .saldo(0.00)
                            .email(null)
                            .address(addressDTO)
                            .codigo(null)
                            .build();

                    clientRegisterRequest.put(tel, clientRegister);
                }
            }
            HashMap<String, UserResponse> response = feignUserClient.createClientsCPVet(clientRegisterRequest, vetClinicId).getBody();

            List<AnimalDTO> animalDTOS = new ArrayList<>();
            resultSet = statement.executeQuery("SELECT * FROM Identificacion");

            while (resultSet.next()) {
                String nameMascota = resultSet.getString("Mascotas");
                Date birthDate = resultSet.getDate("BirthDate");
                String sexo = resultSet.getString("Sexo");
                String especie = resultSet.getString("Especie");
                String raza = resultSet.getString("Raza").trim().toLowerCase();
                String peso = resultSet.getString("Peso");
                String id = resultSet.getString("IDPac");
                String tel = resultSet.getString("Tel");

                if (StringUtils.isNotBlank(tel)) {

                    UserResponse response1 = response.get(tel);

                    AnimalDTO animalDTO = AnimalDTO.builder()
                            .name(nameMascota)
                            .surname(null)
                            .death(false)
                            .vetterCode(id)
                            .sex(sexConverter(sexo))
                            .birthDate(birthDate)
                            .especie(especie)
                            .breed(raza)
                            .ownerUserId(response1.getId())
                            .build();

                    animalDTOS.add(animalDTO);
                }
            }

            List<AnimalMigrationResponse> animalResponses = feignAnimalClient.createAnimals(animalDTOS, vetClinicId).getBody();

            List<ConsultationDTO> consultationDTOS = new ArrayList<>();


            resultSet = statement.executeQuery("SELECT * FROM Historias");

            while (resultSet.next()) {
                String fecha = resultSet.getString("FechaDeLaHistoria");
                String id = resultSet.getString("IDPac");

                String historiaT = resultSet.getString("HistoriaT");
                String tratamiento = resultSet.getString("Tratamiento");

                DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");


                Optional<AnimalMigrationResponse> animal = animalResponses.stream()
                        .filter(animalMigrationResponse -> animalMigrationResponse.getVetterCode().equals(vetClinicId + "-" + id))
                        .findFirst();


                if(animal.isPresent()) {
                    ConsultationDTO consultationDTO = ConsultationDTO.builder()
                            .animalId(animal.get().getId())
                            .details(historiaT)
                            .localDate(fecha != null ? LocalDate.parse(fecha, formatoFecha) : null)
                            .treatment(tratamiento)
                            .build();

                    consultationDTOS.add(consultationDTO);
                }
            }

            feignConsultationsClient.createConsultations(consultationDTOS, vetClinicId, vetUserId);

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

    private Sex sexConverter(String sexo) {
        return sexo.equals("M") ? Sex.FEMALE : Sex.MALE;
    }

}

