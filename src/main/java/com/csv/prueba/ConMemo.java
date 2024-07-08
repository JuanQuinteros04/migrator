package com.csv.prueba;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ConMemo {
    public void test1() {
        Charset stringCharset = Charset.forName("cp1252");

        InputStream dbf = getClass().getClassLoader().getResourceAsStream("data1/clinica/clinica.dbf");
        InputStream memo = getClass().getClassLoader().getResourceAsStream("data1/clinica/clinica3.csv");

        try (DbfReader reader = new DbfReader(dbf, memo)) {
            DbfMetadata meta = reader.getMetadata();
            System.out.println("Read DBF Metadata: " + meta);

            DbfRecord rec;
            while ((rec = reader.read()) != null) {
                rec.setStringCharset(stringCharset);

                System.out.println("CODIGOPACI: " + rec.getString("CODIGOPACI"));

                try {
                    // this reads MEMO field
                    System.out.println("DESCRIP: " + rec.getMemoAsString("DESCRIP"));
                } catch (IOException e) {
                    System.err.println("Error al leer el MEMO: ");
                    e.printStackTrace();
                    // Puedes decidir qué hacer en caso de error, como saltar este registro o continuar
                }

                System.out.println("++++++++++++++++++++++++++++++++++");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}