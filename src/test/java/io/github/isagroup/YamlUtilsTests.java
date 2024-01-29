package io.github.isagroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.yaml.YamlUtils;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class YamlUtilsTests {

    private final static String PATH_TO_PETCLINIC = "yaml-testing/petclinic.yml";
    private final static String PATH_TO_TEST_CONFIG_FILE = "yaml-testing/test.yml";

    @Test
    @Order(1)
    void given_Petclinic_Should_GetPricingManager() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(PATH_TO_PETCLINIC);

        assertTrue(pricingManager.getPlans().get("BASIC") instanceof Plan, "Should be an instance of PricingManager");
        assertEquals(false, pricingManager.getPlans().get("BASIC").getFeatures().get("haveCalendar").getDefaultValue(),
                "The deafult value of the haveCalendar feature should be false");
        assertEquals(2, pricingManager.getPlans().get("BASIC").getFeatures().get("maxPets").getValue(),
                "The value of the maxPets feature should be 2, as it must be copied from the defaultValue");
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

        ;

        assertEquals(List.class.getName(), pricingManager.getPlans().get("BASIC").getFeatures()
                .get("payment")
                .getValue().getClass().getName(),
                "Payment methods should be a list of payment methods");

    }

    @Test
    void given_non_null_features_in_basic_plan_should_have_default_values() {

        // This is the current behaviour of the parser. You have to explicitly write
        // values for BASIC plan
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("parsing/non-null-features-basic.yml");

        Boolean value = (Boolean) pricingManager.getPlans().get("BASIC").getFeatures().get("featureA")
                .getValue();

        assertEquals(false, value);
    }

    @Test
    void given_null_features_in_basic_plan_should_have_default_values() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("parsing/null-features-basic.yml");

        Boolean value = (Boolean) pricingManager.getPlans().get("BASIC").getFeatures().get("featureA")
                .getValue();

        assertEquals(false, value, "BASIC plan does not inherit featureA defaultValue when features is null");
    }

    @Test
    void given_null_features_should_parsed_a_list_of_default_features() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("parsing/null-features-basic.yml");

        boolean value = pricingManager.getPlans().get("BASIC").getFeatures().isEmpty();

        assertEquals(false, value, "Basic plan does not have a list of features");
    }

    @Test
    @Order(2)
    void changeYamlTest() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(PATH_TO_PETCLINIC);

        pricingManager.getPlans().get("BASIC").setMonthlyPrice(1000.0);

        YamlUtils.writeYaml(pricingManager, PATH_TO_TEST_CONFIG_FILE);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }

        PricingManager newPricingManager = YamlUtils.retrieveManagerFromYaml(PATH_TO_TEST_CONFIG_FILE);

        assertEquals(1000.0, newPricingManager.getPlans().get("BASIC").getMonthlyPrice(),
                "The price has not being changed on the yaml");

        newPricingManager.getPlans().get("BASIC").setMonthlyPrice(0.0);

        YamlUtils.writeYaml(newPricingManager, PATH_TO_TEST_CONFIG_FILE);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }

        assertEquals(0.0, newPricingManager.getPlans().get("BASIC").getMonthlyPrice(), "The price has not being reset");

    }

    @Test
    @Order(3)
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

        assertEquals(postman, postmanCopy, "Pricings are diferent");

    }

}
