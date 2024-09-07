package com.liro.migrator.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liro.migrator.dtos.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class AnimalDTO {

    private String name;
    private String surname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date birthDate;
    private Boolean death;
    private Sex sex;
    private Long breedId;
    private Long ownerUserId;
}
