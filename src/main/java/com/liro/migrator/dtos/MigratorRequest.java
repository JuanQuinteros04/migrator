package com.liro.migrator.dtos;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MigratorRequest {

        MultipartFile usersFile;
        MultipartFile animalsFile;
        private Long vetUserId;

}
