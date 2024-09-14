package com.csv;

import com.csv.prueba.PruebaDBFClients;
import com.csv.prueba.PruebaDBFComun;
import com.csv.prueba.PruebaDBFPacientes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.text.ParseException;

@SpringBootApplication
public class MigratorApplication {

	public static void main(String[] args) throws IOException, ParseException {
//		PruebaDBFClients prueba = new PruebaDBFClients();
//		prueba.readDBF();
//		PruebaDBFPacientes pruebaPacientes = new PruebaDBFPacientes();
//		pruebaPacientes.readDBF();
		PruebaDBFComun pruebaDBFComun = new PruebaDBFComun();
		pruebaDBFComun.readDBF();
//		ConMemo prueba2 = new ConMemo();
//		prueba2.test1("7/1");

		SpringApplication.run(MigratorApplication.class);
	}
}
