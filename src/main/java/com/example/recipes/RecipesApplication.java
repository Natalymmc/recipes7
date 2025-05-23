package com.example.recipes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class RecipesApplication {

    public static void main(String[] args) {

        SpringApplication.run(RecipesApplication.class, args);
    }

}
