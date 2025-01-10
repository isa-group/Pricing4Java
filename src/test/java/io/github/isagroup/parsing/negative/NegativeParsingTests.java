package io.github.isagroup.parsing.negative;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import io.github.isagroup.exceptions.InvalidPlanException;
import io.github.isagroup.exceptions.PricingParsingException;
import io.github.isagroup.exceptions.VersionException;
import io.github.isagroup.services.yaml.YamlUtils;

class NegativeParsingTests {

    @ParameterizedTest(name = "{0} - {1}")
    @CsvFileSource(resources = "/negative-parsing-tests.csv", delimiter = ';')
    void negativeTests(String testDescription, String fileName, String expectedErrorMessage) {

        try {
            YamlUtils.retrieveManagerFromYaml(fileName);
            fail();
        } catch (PricingParsingException | InvalidPlanException | VersionException e) {
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

}
