package io.github.isagroup.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import io.github.isagroup.models.Feature;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.featuretypes.Domain;
import io.github.isagroup.models.usagelimittypes.Renewable;
import io.github.isagroup.services.yaml.PricingManagerSerializer;

public class PricingManagerSerializerTest {

    private Yaml yaml;

    @BeforeEach
    public void setUp() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
    }

    @Test
    public void givenNoFeatures_should_ThrowException() {

        PricingManager pricingManager = new PricingManager();

        Map<String, UsageLimit> usageLimits = new LinkedHashMap<>();
        Renewable renewable = new Renewable();
        renewable.setDefaultValue("Bar");
        renewable.setName("foo");
        usageLimits.put("foo", renewable);

        pricingManager.setUsageLimits(usageLimits);
        PricingManagerSerializer pricingManagerSerializer = new PricingManagerSerializer(pricingManager);
        try {
            pricingManagerSerializer.serialize();
            fail("Features are not defined");
        } catch (Exception e) {
            String expected = "Currently you have not defined any features. You may dump a config until you define them";
            assertEquals(expected, e.getMessage());
        }

    }

    @Test
    public void givenNoUsageLimits_should_ThrowException() {

        PricingManager pricingManager = new PricingManager();

        Domain domain = new Domain();
        domain.setName("Foo");
        domain.setDefaultValue("Bar");
        Map<String, Feature> features = new LinkedHashMap<>();
        features.put("Foo", domain);

        pricingManager.setFeatures(features);
        PricingManagerSerializer pricingManagerSerializer = new PricingManagerSerializer(pricingManager);
        try {
            pricingManagerSerializer.serialize();
            fail("Features are not defined");
        } catch (Exception e) {
            String expected = "Currently you have not defined any usage limits. You may dump a config until you define them";
            assertEquals(expected, e.getMessage());
        }

    }

}
