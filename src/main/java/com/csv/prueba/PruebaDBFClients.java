package com.csv.prueba;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;

public class PruebaDBFClients {

    public void readDBF() throws IOException, ParseException {
        Charset stringCharset = Charset.forName("UTF-8");

        InputStream dbf = getClass().getClassLoader().getResourceAsStream("data1/clientes/clientes.dbf");

        DbfRecord rec;
        try (DbfReader reader = new DbfReader(dbf)) {
            DbfMetadata meta = reader.getMetadata();

            System.out.println("Read DBF Metadata: " + meta);
            while ((rec = reader.read()) != null) {
                rec.setStringCharset(stringCharset);
                System.out.println("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
                ClientesLiro cliente = mapToCliente(rec);
                System.out.println("Cliente mapeado: " + cliente);
                System.out.println("------------------------------------------------");

            }
            System.out.println(meta.getFields());

        }
    }
    private ClientesLiro mapToCliente(DbfRecord rec){
        String areaPhoneNumber = rec.getString("TELEFONO");
        String name = rec.getString("NOMBRE");
        String email = rec.getString("EMAIL");
        Integer codigo = rec.getInteger("CODIGO");
        // Crear instancia de Cliente y asignar valores
        ClientesLiro cliente = new ClientesLiro(areaPhoneNumber,name,email,codigo);
        // Puedes asignar otros campos según necesites
        return cliente;
    }
}
