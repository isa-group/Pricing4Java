package io.github.isagroup.parsing.positive;

import org.junit.jupiter.api.Test;

import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.yaml.YamlUtils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;

public class AddOnParserTest {

    private static final String TEST_CASES = "parsing/positive/addOn/";

    @Test
    @DisplayName(value = "'dependsOn': In order to purchase an add-on you have to previosly purchase other add-on.")
    void givenAnAddOnInDependsOnShouldParse() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(TEST_CASES + "dependsOn/dependent-addOn.yml");
        assertEquals(2, pricingManager.getAddOns().size());
        assertTrue(pricingManager.getAddOns().get("baz").getDependsOn().contains("bar"));

    }

    @Test
    @DisplayName(value = "When 'private' is true, add-on should parse")
    void givenEnablePrivateInAddOnShouldParse() {

        String addOnName = "addon1";
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(TEST_CASES + "private/is-private.yml");
        assertTrue(pricingManager.getAddOns().get(addOnName).getIsPrivate());

    }

    @Test
    @DisplayName(value = "When 'private' is false, add-on should parse")
    void givenDisablePrivateInAddOnShouldParse() {

        String addOnName = "addon1";
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(TEST_CASES + "private/is-not-private.yml");
        assertFalse(pricingManager.getAddOns().get(addOnName).getIsPrivate());

    }

    @Test
    @DisplayName(value = "When add-on 'private' is not provided by default is false")
    void givenNullPrivateShouldParse() {

        String addOnName = "addon1";
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(TEST_CASES + "private/no-private.yml");
        assertFalse(pricingManager.getAddOns().get(addOnName).getIsPrivate());

    }

}
