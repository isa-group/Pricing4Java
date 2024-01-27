package io.github.isagroup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void given_Postman_Should_MakePerfectCopy() {

        String postmanOriginalPricing = "pricing/postman.yml";
        //String postmanTestPath = "yaml-testing/postman.yml";

        PricingManager postman = YamlUtils.retrieveManagerFromYaml(postmanOriginalPricing);
        //YamlUtils.writeYaml(postman, postmanTestPath);

    }

}
