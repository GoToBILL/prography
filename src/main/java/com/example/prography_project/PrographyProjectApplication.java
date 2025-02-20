package com.example.prography_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PrographyProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrographyProjectApplication.class, args);
    }

}
