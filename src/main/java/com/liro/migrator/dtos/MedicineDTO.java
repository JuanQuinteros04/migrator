package com.liro.migrator.dtos;

import com.liro.migrator.dtos.enums.AnimalType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class MedicineDTO {

    private String commercialName;
    private String formalName;
    private Boolean onlyVetUse;
    private Boolean needPrescription;
    private String conservation;
    private String dosesUnity;
    private AnimalType animalType;


    private String brandName;
    private String medicineType;
    private Set<String> medicineGroups;
    private Set<String> components;

    private String presentationName;
}
