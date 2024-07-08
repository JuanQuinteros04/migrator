package com.csv.prueba;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class PruebaDBFPacientes {

    public void readDBF() throws IOException, ParseException {
        Charset stringCharset = Charset.forName("UTF-8");

        InputStream dbf = getClass().getClassLoader().getResourceAsStream("data1/pacientes/pacientes.dbf");

        DbfRecord rec;
        try (DbfReader reader = new DbfReader(dbf)) {
            DbfMetadata meta = reader.getMetadata();

            System.out.println("Read DBF Metadata: " + meta);
            while ((rec = reader.read()) != null) {
                rec.setStringCharset(stringCharset);
                System.out.println("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
                try {
                    PacientesLiro paciente = mapToPaciente(rec);
                    System.out.println("Paciente mapeado: " + paciente);
                    System.out.println("------------------------------------------------");
                }catch (Exception e){
                    System.out.println("Error juanma: " + e);
                }
            }
//            System.out.println(meta.getFields());

        }
    }
    private PacientesLiro mapToPaciente(DbfRecord rec) throws ParseException {
        String name = rec.getString("NOMBRE");
        String sex = rec.getString("SEXO");
        // Manejar el caso de fecha nula
        Date date = rec.getDate("FECHA_NAC");
        LocalDate birthDate = (date != null) ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        boolean death = !rec.getBoolean("VIVE");
        String photo = rec.getString("FOTO");

        // Crear instancia de Cliente y asignar valores
        PacientesLiro paciente = new PacientesLiro(name,sex,birthDate,death, photo);
        // Puedes asignar otros campos según necesites
        return paciente;
    }

}
