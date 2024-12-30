package io.github.isagroup.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.yaml.YamlUtils;

public class YamlParsingTests {
    
    @ParameterizedTest
    @CsvFileSource(resources = "/full-parsing-tests.csv", delimiter = ';')
    void fullParsingTests(String filePath, String expectedSaaSName) {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(filePath);

        assertEquals(expectedSaaSName, pricingManager.getSaasName(), "The saasName should be " + expectedSaaSName);
    }
}