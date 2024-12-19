package io.github.isagroup.parsing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.yaml.snakeyaml.Yaml;

import io.github.isagroup.exceptions.InvalidPlanException;
import io.github.isagroup.exceptions.PricingParsingException;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.parsing.PricingManagerParser;
import io.github.isagroup.services.yaml.YamlUtils;

import static org.junit.jupiter.api.Assertions.*;

public class AddOnParserTest {

    private final static String ADD_ONS_TEST_CASES_PATH = "parsing/add-ons/";
    private final static String NEGATIVE_CASES = ADD_ONS_TEST_CASES_PATH + "negative/";

    @Test
    void givenInvalidSyntaxAddOnUsageLimitShouldThrowException() {

        Exception ex = assertThrows(PricingParsingException.class,
                () -> YamlUtils
                        .retrieveManagerFromYaml(NEGATIVE_CASES + "add-ons-usage-limits-not-a-map.yml"));
        assertEquals("The usage limit fooLimit of the add-on Baz is not a valid map. Maybe 'value' attribute is missing to set the value of the limit", ex.getMessage());
    }

    @Test
    void givenInvalidSyntaxAddOnUsageLimitExtensionsShouldThrowException() {

        Exception ex = assertThrows(PricingParsingException.class,
                () -> YamlUtils
                        .retrieveManagerFromYaml(NEGATIVE_CASES + "add-ons-usage-limits-extensions-not-a-map.yml"));
        assertEquals("The usage limit fooLimit of the add-on Baz is not a valid map. Maybe 'value' attribute is missing to set the value of the limit", ex.getMessage());
    }

    @Test
    void givenAddOnThatDependsOnAnotherAddonCreatesPricingManager() {

        String path = "parsing/rules/positive/add-on-depending-on-another-add-on.yml";
        try {
            PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(path);
            assertEquals(2, pricingManager.getAddOns().size());
            assertTrue(pricingManager.getAddOns().get("baz").getDependsOn().contains("bar"));
        } catch (PricingParsingException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void givenUndefinedAddOnInASpecificAddOnShouldThrow() {

        String path = "parsing/rules/negative/add-on-depending-on-unexistent-add-on.yml";
        try {
            YamlUtils.retrieveManagerFromYaml(path);
            fail();
        } catch (InvalidPlanException e) {
            assertEquals("The plan or addOn bax is not defined in the pricing manager", e.getMessage());
        }
    }


}
