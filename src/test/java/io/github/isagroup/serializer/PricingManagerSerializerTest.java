package io.github.isagroup.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import io.github.isagroup.models.AddOn;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.Plan;
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

    private PricingManager initPricingManager() {
        PricingManager pricingManager = new PricingManager();

        Domain domain = new Domain();
        domain.setName("domain");
        domain.setDefaultValue("Bar");
        Map<String, Feature> features = new LinkedHashMap<>();
        features.put("bar", domain);
        pricingManager.setFeatures(features);

        Map<String, UsageLimit> usageLimits = new LinkedHashMap<>();
        Renewable renewable = new Renewable();
        renewable.setName("Renewable");
        renewable.setDefaultValue("Bar");
        usageLimits.put("bar", renewable);
        pricingManager.setUsageLimits(usageLimits);

        Map<String, Plan> plans = new LinkedHashMap<>();
        Plan plan = new Plan();
        plan.setName("BASIC");
        plans.put("BASIC", plan);
        pricingManager.setPlans(plans);

        Map<String, AddOn> addOns = new LinkedHashMap<>();
        AddOn addOn = new AddOn();
        addOn.setName("fooAddOn");
        addOns.put("fooAddOn", addOn);
        pricingManager.setAddOns(addOns);

        return pricingManager;
    }

    @Test
    public void givenNoFeatures_should_ThrowException() {

        PricingManager pricingManager = initPricingManager();

        pricingManager.setFeatures(null);
        PricingManagerSerializer pricingManagerSerializer = new PricingManagerSerializer(pricingManager);
        try {
            pricingManagerSerializer.serialize();
            fail("Features are not defined");
        } catch (Exception e) {
            String expected = "Currently you have not defined any features. You may not dump config until you define them";
            assertEquals(expected, e.getMessage());
        }

    }

    @Test
    public void givenNoUsageLimits_should_ThrowException() {

        PricingManager pricingManager = initPricingManager();

        pricingManager.setUsageLimits(null);
        PricingManagerSerializer pricingManagerSerializer = new PricingManagerSerializer(pricingManager);
        try {
            pricingManagerSerializer.serialize();
            fail("Features are not defined");
        } catch (Exception e) {
            String expected = "Currently you have not defined any usage limits. You may not dump a config until you define them";
            assertEquals(expected, e.getMessage());
        }

    }

    @Test
    public void givenNoPlans_should_ThrowException() {

        PricingManager pricingManager = initPricingManager();
        pricingManager.setPlans(null);

        PricingManagerSerializer pricingManagerSerializer = new PricingManagerSerializer(pricingManager);
        try {
            pricingManagerSerializer.serialize();
            fail("Features are not defined");
        } catch (Exception e) {
            String expected = "Currently you have not defined any plans. You may not dump a config until you define them";
            assertEquals(expected, e.getMessage());
        }

    }

    @Test
    public void givenNoAddOns_should_SerializeNullAddOns() {

        PricingManager pricingManager = initPricingManager();

        PricingManagerSerializer pricingManagerSerializer = new PricingManagerSerializer(pricingManager);
        try {
            Map<String, Object> res = pricingManagerSerializer.serialize();
            Object actual = res.getOrDefault("addOns", "Fail");
            assertEquals(null, actual, "addOns does not serialized as null value");
        } catch (Exception e) {
            fail("addOns key does not exist");

        }

    }

}
