package com.liro.migrator.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class AddressDTO {


    private String addressLine1;

    private String addressLine2;

    private String city;

    private String location;

    private String country;

    private String postalCode;
}
