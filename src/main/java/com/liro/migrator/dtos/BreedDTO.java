package com.liro.migrator.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class BreedDTO {

    private String name;
    private String formalName;
    private String details;
    private Set<String> alternativeNames;
    private Long animalTypeId;
}
