package com.liro.migrator.dtos;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MigratorRequest {

        byte[] usersFile;
        byte[] animalsFile;
        byte[] clinicaFile;
        byte[] clinicaFileFTP;

        private Long vetUserId;

}
