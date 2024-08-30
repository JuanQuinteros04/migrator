package com.csv.prueba;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;


public class ConMemo {
    public void test1(String codigoPaci) {
        Charset stringCharset = Charset.forName("cp1252");

        InputStream dbf = getClass().getClassLoader().getResourceAsStream("data1/clinica/clinica.dbf");
        InputStream memo = getClass().getClassLoader().getResourceAsStream("data1/clinica/clinica.FPT");

        try (DbfReader reader = new DbfReader(dbf, memo)) {
            DbfMetadata meta = reader.getMetadata();
            System.out.println("Read DBF Metadata: " + meta);

            DbfRecord rec;
            while ((rec = reader.read()) != null) {
                    rec.setStringCharset(stringCharset);

                    String numeroPaciActual = rec.getString("CODIGOPACI");
//                    System.out.println(numeroPaciActual);

                    if(codigoPaci.equals(numeroPaciActual)){
                        System.out.println("CODIGOPACI: " + rec.getString("CODIGOPACI"));
                        // this reads MEMO field
                        System.out.println("DESCRIP: " + rec.getMemoAsString("DESCRIP"));

                        System.out.println("++++++++++++++++++++++++++++++++++");}else {
//                        System.out.println("El codigo: " + numeroPaciActual + " no es valido");
                    }
            }

//            System.out.println(meta.getFields());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}