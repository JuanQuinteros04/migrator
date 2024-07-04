package com.csv.prueba;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;

public class ConMemo {
    public static void main(String[] args) throws IOException {
        Enumeration<URL> resources = ConMemo.class.getClassLoader().getResources("");
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            System.out.println("Resource: " + resource);
        }

        new ConMemo().test1();
    }

    public void test1() {
        Charset stringCharset = Charset.forName("cp1252");

        try {
            // Lee el archivo DBF completamente en memoria
            InputStream dbfInputStream = getClass().getClassLoader().getResourceAsStream("data1/clinica.dbf");
            if (dbfInputStream == null) {
                throw new IOException("DBF file not found");
            }
            byte[] dbfData = readFully(dbfInputStream);
            ByteArrayInputStream dbfInMemory = new ByteArrayInputStream(dbfData);

            // Lee el archivo MEMO completamente en memoria
            InputStream memoInputStream = getClass().getClassLoader().getResourceAsStream("data1/clinica.fpt");
            if (memoInputStream == null) {
                throw new IOException("MEMO file not found");
            }
            byte[] memoData = readFully(memoInputStream);
            ByteArrayInputStream memoInMemory = new ByteArrayInputStream(memoData);

            try (DbfReader reader = new DbfReader(dbfInMemory, memoInMemory)) {
                DbfMetadata meta = reader.getMetadata();
                System.out.println("Read DBF Metadata: " + meta);

                // Imprime los nombres de los campos disponibles
                meta.getFields().forEach(field -> System.out.println("Field: " + field.getName()));

                DbfRecord rec;
                while ((rec = reader.read()) != null) {
                    rec.setStringCharset(stringCharset);

                    System.out.println("CODIGOPACI: " + rec.getString("CODIGOPACI"));
                    // Este campo es de tipo MEMO
                    System.out.println("DESCRIP: " + rec.getMemoAsString("DESCRIP"));
                    System.out.println("++++++++++++++++++++++++++++++++++");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readFully(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }
}