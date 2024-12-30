package io.github.isagroup.parsing;

import org.junit.jupiter.api.Test;

import io.github.isagroup.exceptions.PricingParsingException;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.yaml.YamlUtils;

import static org.junit.jupiter.api.Assertions.*;

public class AddOnParserTest {

    @Test
    void givenAddOnThatDependsOnAnotherAddonCreatesPricingManager() {

        String path = "parsing/legacy-tests/rules/positive/add-on-depending-on-another-add-on.yml";
        try {
            PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(path);
            assertEquals(2, pricingManager.getAddOns().size());
            assertTrue(pricingManager.getAddOns().get("baz").getDependsOn().contains("bar"));
        } catch (PricingParsingException e) {
            fail(e.getMessage());
        }
    }

}
