package com.liro.migrator.service;

import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConMemo {
    public void test1() {

        InputStream dbf = getClass().getClassLoader().getResourceAsStream("data1/clinica.dbf");


        try (DBFReader reader = new DBFReader(dbf)) {

            File dbfMemoFile = new File("C:\\Users\\Usuario\\read-write\\src\\main\\resources\\data1\\clinica.FPT");

            reader.setMemoFile(dbfMemoFile);
            int numberOfFields = reader.getFieldCount();

            // use this count to fetch all field information
            // if required

            for (int i = 0; i < numberOfFields; i++) {

                DBFField field = reader.getField(i);

                // do something with it if you want
                // refer the JavaDoc API reference for more details
                //
                System.out.println(field.getName());
            }

            // Now, lets us start reading the rows

            DBFRow row = reader.nextRow();

            while (row != null) {

                parseMedicalRecords(row.getString("DESCRIP"));

                row = reader.nextRow();
            }

        }
    }

    private void parseMedicalRecords(String input) {
        // Separar las secciones del texto usando "******"
        String[] sections = input.split("\\*{6}");
        for (String section : sections) {
            if (section.trim().isEmpty()) {
                continue; // Ignorar secciones vacías
            }

            // Extraer la fecha
            String fechaRegex = "Fecha: (\\d{2}/\\d{2}/\\d{4})";
            Matcher fechaMatcher = Pattern.compile(fechaRegex).matcher(section);
            String fecha = fechaMatcher.find() ? fechaMatcher.group(1) : "No encontrada";

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


            System.out.println("Fecha: " + fecha);
            System.out.println("Atendido Por: " + atendidoPor);
            System.out.println("Peso: " + peso);
            System.out.println("Descripción: " + description);
            System.out.println("--------------------------------------------------");
        }


    }
}