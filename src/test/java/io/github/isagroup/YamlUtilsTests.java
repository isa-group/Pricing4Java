package io.github.isagroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.yaml.YamlUtils;

public class YamlUtilsTests {

    private final static String PATH_TO_PETCLINIC = "pricing/petclinic.yml";

    @Test
    void given_Petclinic_Should_GetPricingManager() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(PATH_TO_PETCLINIC);

        assertTrue(pricingManager.getPlans().get("BASIC") instanceof Plan, "Should be an instance of PricingManager");
        assertEquals(false, pricingManager.getPlans().get("BASIC").getFeatures().get("haveCalendar").getDefaultValue(),
                "The deafult value of the haveCalendar feature should be false");
        assertEquals(null, pricingManager.getPlans().get("BASIC").getFeatures().get("maxPets").getValue(),
                "The value of the maxPets should be null");

    }

    @Test
    void given_payment_feature_defaultValue_should_be_list_of_payment_method() {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("parsing/payment-feature.yml");

        List<String> expectedPaymentMethods = new ArrayList<>();
        expectedPaymentMethods.add("CARD");
        List<String> actualPaymentMethods = (List<String>) pricingManager.getFeatures().get("payment")
                .getDefaultValue();

        assertEquals(expectedPaymentMethods, actualPaymentMethods,
                "Payment methods should be a list of payment methods");

    }

    @Test
    void given_payment_feature_basic_plan_value_should_a_list() {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("parsing/payment-feature.yml");

        List<String> overwrittenPaymentMethods = (List<String>) pricingManager.getPlans().get("BASIC").getFeatures()
                .get("payment")
                .getValue();

        assertInstanceOf(List.class, overwrittenPaymentMethods,
                "Payment methods is not a list of payment methods");
        assertEquals(2, overwrittenPaymentMethods.size());

    }

    @Test
    void given_non_null_features_in_basic_plan_should_have_default_values() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("parsing/non-null-features-basic.yml");

        Boolean value = (Boolean) pricingManager.getPlans().get("BASIC").getFeatures().get("featureA")
                .getValue();

        assertEquals(true, value);
    }

    @Test
    void given_null_features_in_basic_plan_should_have_default_values() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("parsing/null-features-basic.yml");

        Boolean value = (Boolean) pricingManager.getPlans().get("BASIC").getFeatures().get("featureA")
                .getValue();

        assertNull(value, "BASIC plan value is not null");
    }

    @Test
    void given_null_features_should_parsed_a_list_of_default_features() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("parsing/null-features-basic.yml");

        boolean value = pricingManager.getPlans().get("BASIC").getFeatures().isEmpty();

        assertEquals(false, value, "Basic plan does not have a list of features");
    }

    @Test
    void given_pricing_should_dump_a_copy() {

        String postmanOriginalPricing = "pricing/postman.yml";
        String postmanTestPath = "yaml-testing/postman.yml";

        PricingManager postman = YamlUtils.retrieveManagerFromYaml(postmanOriginalPricing);
        YamlUtils.writeYaml(postman, postmanTestPath);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        PricingManager postmanCopy = YamlUtils.retrieveManagerFromYaml(postmanTestPath);

        assertEquals(postman.getFeatures(), postmanCopy.getFeatures(), "Pricings are diferent");

    }

}
