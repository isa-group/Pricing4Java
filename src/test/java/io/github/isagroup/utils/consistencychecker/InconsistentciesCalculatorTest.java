package io.github.isagroup.utils.consistencychecker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.yaml.YamlUtils;

public class InconsistentciesCalculatorTest {

    @Test
    @DisplayName("Given a pricing should detect enabled features - disabled usagelimits and disabled features - enabled usage limits")
    void givenAPricingShouldDetectInconsistencies() {

        List<FeatUsageLimitInfo> expected = new ArrayList<>();
        expected.add(new FeatUsageLimitInfo("usageLimit1", 25, "feature1", false));
        expected.add(new FeatUsageLimitInfo("usageLimit2", 0, "feature2", true));

        PricingManager pr = YamlUtils.retrieveManagerFromYaml(
                Path.of("pricing", "inconsistent-pricing.yml").toString());
        InconsistentciesCalculator inconsistenciesCalc = new InconsistentciesCalculator(pr);

        if (inconsistenciesCalc.getInconsistentItems().isEmpty()) {
            fail("Expected a pricing with inconsistencies");
        }

        assertEquals(expected, inconsistenciesCalc.getInconsistentItems().get().getInconsistent());
    }

    @Test
    void givenPricingWithFeatureEnabledAndUsageLimitDisabledShouldTransform() {
        PricingManager pricing = YamlUtils.retrieveManagerFromYaml(
                Path.of("pricing", "inconsistent-pricing.yml").toString());
        InconsistentciesCalculator inconsistenciesCalc = new InconsistentciesCalculator(pricing);

        if (inconsistenciesCalc.getInconsistentItems().isEmpty()) {
            fail("I should have found inconsistencies");
        }

        inconsistenciesCalc.transformPricing(pricing);

        assertTrue((Boolean) pricing.getPlans().get("ALPHA").getFeatures().get("feature2").getValue());
        assertTrue((Boolean) pricing.getPlans().get("BETA").getFeatures().get("feature2").getValue());
        assertTrue((Boolean) pricing.getPlans().get("CHARLIE").getFeatures().get("feature2").getValue());

        assertEquals(10, pricing.getPlans().get("ALPHA").getUsageLimits().get("usageLimit2").getValue());
        assertEquals(30, pricing.getPlans().get("BETA").getUsageLimits().get("usageLimit2").getValue());
        assertEquals(50, pricing.getPlans().get("CHARLIE").getUsageLimits().get("usageLimit2").getValue());
    }

    @Test
    void givenPricingWithFeatureDisabledAndUsageLimitEnabledShouldTransform() {
        PricingManager pricing = YamlUtils.retrieveManagerFromYaml(
                Path.of("pricing", "inconsistent-pricing.yml").toString());
        InconsistentciesCalculator inconsistenciesCalc = new InconsistentciesCalculator(pricing);

        if (inconsistenciesCalc.getInconsistentItems().isEmpty()) {
            fail("I should have found inconsistencies");
        }

        inconsistenciesCalc.transformPricing(pricing);

        assertEquals(0, pricing.getUsageLimits().get("usageLimit1").getDefaultValue());

        assertNull(pricing.getPlans().get("ALPHA").getFeatures().get("feature1").getValue());
        assertTrue((Boolean) pricing.getPlans().get("BETA").getFeatures().get("feature1").getValue());
        assertTrue((Boolean) pricing.getPlans().get("CHARLIE").getFeatures().get("feature1").getValue());

        assertNull(pricing.getPlans().get("ALPHA").getUsageLimits().get("usageLimit1").getValue());
        assertEquals(25, pricing.getPlans().get("BETA").getUsageLimits().get("usageLimit1").getValue());
        assertEquals(50, pricing.getPlans().get("CHARLIE").getUsageLimits().get("usageLimit1").getValue());
    }

}
