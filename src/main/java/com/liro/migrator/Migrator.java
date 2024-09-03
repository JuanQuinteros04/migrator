package com.liro.migrator;

import com.liro.migrator.service.ClientsMigrator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class Migrator {

    public static void main(String[] args) {


        SpringApplication.run(Migrator.class);
    }
}
