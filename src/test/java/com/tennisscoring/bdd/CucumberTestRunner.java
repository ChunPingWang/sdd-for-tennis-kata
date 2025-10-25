package com.tennisscoring.bdd;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Cucumber test runner for BDD scenarios.
 * Cucumber 測試運行器，用於執行 BDD 情境
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.tennisscoring.bdd")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty,html:target/cucumber-reports")
public class CucumberTestRunner {
    // This class serves as the test runner for Cucumber BDD scenarios
    // 此類別作為 Cucumber BDD 情境的測試運行器
}