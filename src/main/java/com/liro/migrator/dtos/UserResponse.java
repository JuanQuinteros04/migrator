package com.liro.migrator.dtos;

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
public class UserResponse extends UserDTO {

    private Long id;
    private Set<AddressDTO> addresses;

    private String codigoVetter;
}
