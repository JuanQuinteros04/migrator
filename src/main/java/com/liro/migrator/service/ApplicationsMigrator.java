package com.liro.migrator.service;

import com.liro.migrator.dtos.ApplicationDTO;
import com.liro.migrator.dtos.MedicineDTO;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class ApplicationsMigrator {

    public void migrateApplications(MultipartFile file) throws IOException, CsvException {


    }
}
