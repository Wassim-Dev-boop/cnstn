package com.alnaifer.nehb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application principale Nehb - Plateforme Éducative AlNaifer
 * LMS & ERP pour écoles coraniques
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class NehbApplication {

    public static void main(String[] args) {
        SpringApplication.run(NehbApplication.class, args);
    }
}
