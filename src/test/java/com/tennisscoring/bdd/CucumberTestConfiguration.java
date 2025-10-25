package com.tennisscoring.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Cucumber test configuration for Spring Boot integration.
 * Cucumber 測試配置，用於 Spring Boot 整合
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberTestConfiguration {
    // This class serves as the Spring Boot test configuration for Cucumber
    // 此類別作為 Cucumber 的 Spring Boot 測試配置
}