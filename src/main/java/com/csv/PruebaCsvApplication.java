package com.csv;

import com.csv.prueba.ConMemo;
import com.csv.prueba.Prueba;
import com.opencsv.CSVReader;
import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;

@SpringBootApplication
public class PruebaCsvApplication {

	public static void main(String[] args) throws IOException, ParseException {
//		Prueba prueba = new Prueba();
//		prueba.readDBF();

		ConMemo prueba2 = new ConMemo();
		prueba2.test1();
		SpringApplication.run(PruebaCsvApplication.class);
	}
}
