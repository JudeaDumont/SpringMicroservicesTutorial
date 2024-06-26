package com.example.inventory_service;

import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    //This will create duplicates everytime the service is started but I would rather have that then set
    //	spring.jpa.hibernate.ddl-auto=create-drop
    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {
            inventoryRepository.save(Inventory.builder()
                    .skuCode("test1")
                    .quantity(1)
                    .build());
            inventoryRepository.save(Inventory.builder()
                    .skuCode("test2")
                    .quantity(0)
                    .build());
        };
    }
}
