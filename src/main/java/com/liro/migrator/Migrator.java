package com.liro.migrator;

import com.liro.migrator.service.ClientsMigrator;
import com.liro.migrator.service.MigratorDequeuer;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.io.IOException;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
@EnableScheduling
public class Migrator {

    public static void main(String[] args) {

        SpringApplication.run(Migrator.class);

    }
}
