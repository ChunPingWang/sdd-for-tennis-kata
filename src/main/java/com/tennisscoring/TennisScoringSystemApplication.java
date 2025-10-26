package com.tennisscoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for Tennis Scoring System.
 * 網球計分系統的 Spring Boot 主應用程式類別
 * 
 * This application provides a REST API for managing tennis match scoring
 * using hexagonal architecture principles.
 * 
 * 此應用程式使用六角形架構原則提供管理網球比賽計分的 REST API。
 */
@SpringBootApplication
public class TennisScoringSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TennisScoringSystemApplication.class, args);
    }
}