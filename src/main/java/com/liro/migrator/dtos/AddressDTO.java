package com.liro.migrator.dtos;


import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class AddressDTO {


    private String addressLine1;

    private String addressLine2;

    private String city;

    private String location;

    private String country;

    private String postalCode;
}
