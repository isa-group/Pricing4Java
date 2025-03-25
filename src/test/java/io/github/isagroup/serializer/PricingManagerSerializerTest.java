package io.github.isagroup.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.isagroup.models.Feature;
import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.models.UsageLimit;
import io.github.isagroup.models.featuretypes.Domain;
import io.github.isagroup.models.usagelimittypes.Renewable;
import io.github.isagroup.services.serializer.PricingManagerSerializer;

class PricingManagerSerializerTest {

    private PricingManager initPricingManager() {
        PricingManager pricingManager = new PricingManager();

        pricingManager.setCreatedAt(LocalDate.now());

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

        return pricingManager;
    }

    @Test
    @DisplayName("When features are not provided it should not serialize")
    void givenNoFeaturesShouldThrowException() {

        PricingManager pricingManager = initPricingManager();

        pricingManager.setFeatures(null);
        PricingManagerSerializer pricingManagerSerializer = new PricingManagerSerializer();
        try {
            pricingManagerSerializer.serialize(pricingManager);
            fail("Features are not defined");
        } catch (Exception e) {
            String expected = "Features are null. Filling the pricing with features is mandatory.";
            assertEquals(expected, e.getMessage());
        }

    }

    @Test
    @DisplayName("At least plans or add-ons should be provided otherwise serializing fails")
    void givenNoPlansAndAddOnsShouldThrowException() {

        PricingManager pricingManager = initPricingManager();
        pricingManager.setPlans(null);

        PricingManagerSerializer pricingManagerSerializer = new PricingManagerSerializer();
        try {
            pricingManagerSerializer.serialize(pricingManager);
            fail("Plans are not defined");
        } catch (Exception e) {
            String expected = "Plans and AddOns are null. You have to set at least one of them.";
            assertEquals(expected, e.getMessage());
        }

    }

    @Test
    @DisplayName("Dump null when add-ons are not present in PricingManager")
    void givenNoAddOnsShouldSerializeNullAddOns() {

        PricingManager pricingManager = initPricingManager();

        PricingManagerSerializer pricingManagerSerializer = new PricingManagerSerializer();
        try {
            Map<String, Object> res = pricingManagerSerializer.serialize(pricingManager);
            assertEquals(null, res.get("addOns"));
        } catch (Exception e) {
            fail("addOns key does not exist");

        }

    }

    @Test
    @DisplayName("Optional root pricing fields should not be dumped")
    void givenNoValuesInPlanOptionalPropertiesShouldNotSerializeThem() {

        PricingManager pricingManager = new PricingManager();
        pricingManager.setCreatedAt(LocalDate.now());

        PricingManagerSerializer pricingManagerSerializer = new PricingManagerSerializer();

        Domain domain = new Domain();
        domain.setName("domain");
        domain.setDefaultValue("Bar");
        Map<String, Feature> features = new LinkedHashMap<>();
        features.put("bar", domain);
        pricingManager.setFeatures(features);

        Map<String, Plan> plans = new LinkedHashMap<>();
        Plan plan = new Plan();
        plan.setName("BASIC");
        plans.put("BASIC", plan);
        pricingManager.setPlans(plans);

        Map<String, Object> res = pricingManagerSerializer.serialize(pricingManager);
        assertFalse(res.containsKey("url"));
        assertFalse(res.containsKey("tags"));
        assertFalse(res.containsKey("variables"));
    }
}
