package com.example.tangerine.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(title = "Tangerine API", version = "0.1",
        description = """
            An API of CRM for culinary masters.
            Application implements abilities to create, read, manage and other
            general manipulations with users, recipes, menus, ingredients and comments."""
    )
)
public class TangerineApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(TangerineApiApplication.class, args);
  }
}
