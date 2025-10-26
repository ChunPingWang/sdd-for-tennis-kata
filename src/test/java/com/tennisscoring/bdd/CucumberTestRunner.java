package com.tennisscoring.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Cucumber test runner for BDD scenarios.
 * Cucumber 測試運行器，用於執行 BDD 情境
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.tennisscoring.bdd",
    plugin = {"pretty", "html:target/cucumber-reports"}
)
public class CucumberTestRunner {
    // This class serves as the test runner for Cucumber BDD scenarios
    // 此類別作為 Cucumber BDD 情境的測試運行器
}