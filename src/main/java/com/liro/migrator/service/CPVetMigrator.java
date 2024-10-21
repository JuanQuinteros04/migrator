package com.liro.migrator.service;

import com.liro.migrator.dtos.AnimalMigrationResponse;
import com.liro.migrator.dtos.UserResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class CPVetMigrator {


    private static final String DIRECTORY_PATH = "/liro/cpvet/migrate";
    private static final String PROCESSED_FOLDER_PATH = "/liro/cpvet/processed";


    public CPVetMigrator() {
    }

    @Scheduled(fixedRate = 6000000)
    public void processFiles() throws IOException {
        File folder = new File(DIRECTORY_PATH);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".zip"));

        if (files == null) {
            System.out.println("Not files found");
            return;
        }


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
                String url = "jdbc:ucanaccess://" + databasePath;

                // Establish the connection
                connection = DriverManager.getConnection(url);

                // Create a statement
                statement = connection.createStatement();

                // Execute a query to read data from the table
                resultSet = statement.executeQuery("SELECT * FROM Identificacion");

                // Process the result set
                while (resultSet.next()) {
                    System.out.println("Column1: " + resultSet.getString("Propietario"));
                    System.out.println("Column1: " + resultSet.getString("Domicilio"));
                    System.out.println("Column1: " + resultSet.getString("Tel"));

                    // Add more columns if needed
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
            String vetUserIdsStr = fileName.replace(".zip", "");

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
}
