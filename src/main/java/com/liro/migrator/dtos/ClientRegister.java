package com.liro.migrator.dtos;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.liro.migrator.dtos.AddressDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString

public class ClientRegister {

    private String name;
    private String surname;
    private String phoneNumber;
    private String areaPhoneNumber;
    private String email;
    private AddressDTO address;
    private Double saldo;
    private String codigo;
}