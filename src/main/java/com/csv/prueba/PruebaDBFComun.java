package com.csv.prueba;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;

public class PruebaDBFComun {

    public void readDBF() throws IOException, ParseException {
        Charset stringCharset = Charset.forName("Cp866");

        InputStream dbf = getClass().getClassLoader().getResourceAsStream("data1/clinica/pruebaguardado.csv");

        DbfRecord rec;
        try (DbfReader reader = new DbfReader(dbf)) {
            DbfMetadata meta = reader.getMetadata();

            System.out.println("Read DBF Metadata: " + meta);
            while ((rec = reader.read()) != null) {
                rec.setStringCharset(stringCharset);
                System.out.println("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
            }
        }
    }

}
