package io.github.isagroup;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.github.isagroup.models.Feature;
import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.models.ValueType;
import io.github.isagroup.models.featuretypes.Domain;
import io.github.isagroup.services.yaml.YamlUtils;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PricingServiceTests {

    private static final String JWT_SECRET_TEST = "secret";
    private static final Integer JWT_EXPIRATION_TEST = 86400;
    private static final String JWT_SUBJECT_TEST = "admin1";
    private static final String PETCLINIC_CONFIG_PATH = "pricing/petclinic.yml";

    private static final String TEMPORAL_CONFIG_PATH = "yaml-testing/temp.yml";

    private static final String TEST_PLAN = "BASIC";
    private static final String TEST_NEW_PLAN = "NEW_PLAN";
    private static Plan newPlan = new Plan();
    private static final String TEST_BOOLEAN_ATTRIBUTE = "haveCalendar";
    private static final String TEST_NUMERIC_ATTRIBUTE = "maxPets";
    private static final String TEST_TEXT_ATTRIBUTE = "supportPriority";
    private static final String NEW_FEATURE_NAME = "newFeature";
    private static final String NEW_FEATURE_TEST_VALUE = "testValue";
    private static final String NEW_FEATURE_TEST_EXPRESSION = "userContext['pets'] > 1";
    private static final PricingManager ORIGINAL_PRICING_MANAGER = YamlUtils
            .retrieveManagerFromYaml(PETCLINIC_CONFIG_PATH);

    private PricingService pricingService;

    private PricingContextTestImpl pricingContextTestImpl;

    @BeforeAll
    static void setUp() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(PETCLINIC_CONFIG_PATH);

        newPlan.setDescription("New plan description");
        newPlan.setMonthlyPrice(2.0);

        Map<String, Feature> features = pricingManager.getPlans().get(TEST_PLAN).getFeatures();

        Feature newBooleanFeature = features.get(TEST_BOOLEAN_ATTRIBUTE);
        Feature newNumericFeature = features.get(TEST_NUMERIC_ATTRIBUTE);
        Feature newTextFeature = features.get(TEST_TEXT_ATTRIBUTE);

        newBooleanFeature.setValue(true);
        newNumericFeature.setValue(6);
        newTextFeature.setValue("HIGH");

        features.put(TEST_BOOLEAN_ATTRIBUTE, newBooleanFeature);
        features.put(TEST_NUMERIC_ATTRIBUTE, newNumericFeature);
        features.put(TEST_TEXT_ATTRIBUTE, newTextFeature);

        newPlan.setFeatures(features);

    }

    @BeforeEach
    public void init() {

        // Read petclinic
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(PETCLINIC_CONFIG_PATH);
        // Write temporal petclinic config file
        YamlUtils.writeYaml(pricingManager, TEMPORAL_CONFIG_PATH);

        Map<String, Object> userContext = new HashMap<>();
        userContext.put("username", JWT_SUBJECT_TEST);
        userContext.put("pets", 2);

        Map<String, Object> userAuthorities = new HashMap<>();
        userAuthorities.put("role", "admin");
        userAuthorities.put("username", JWT_SUBJECT_TEST);
        userAuthorities.put("password", "4dm1n");

        PricingContextTestImpl pricingContextTestImpl = new PricingContextTestImpl();
        pricingContextTestImpl.setConfigFilePath(TEMPORAL_CONFIG_PATH);
        pricingContextTestImpl.setJwtExpiration(JWT_EXPIRATION_TEST);
        pricingContextTestImpl.setJwtSecret(JWT_SECRET_TEST);
        pricingContextTestImpl.setUserPlan(TEST_PLAN);
        pricingContextTestImpl.setUserAuthorities(userAuthorities);
        pricingContextTestImpl.setUserContext(userContext);

        this.pricingContextTestImpl = pricingContextTestImpl;
        this.pricingService = new PricingService(pricingContextTestImpl);
    }

    @AfterEach
    void after() {
        try {
            File file = new File("src/test/resources/yaml-testing/temp.yml");
            file.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------------- PLAN RETRIEVAL ---------------------------

    @Test
    @Order(10)
    void should_ReturnPlan_given_PlanName() {

        Plan plan = pricingService.getPlanFromName("BASIC");

        assertInstanceOf(Plan.class, plan);

        assertEquals(0.0, plan.getAnnualPrice());
        assertEquals(0.0, plan.getMonthlyPrice());

    }

    @Test
    @Order(20)
    void should_ThrowException_given_NonExistentPlan() {

        String nonExistentPlan = "nonExistentPlan";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.getPlanFromName(nonExistentPlan);
        });

        assertEquals("The plan " + nonExistentPlan + " does not exist in the current pricing configuration",
                exception.getMessage());

    }

    // // --------------------------- PLAN ADITION ---------------------------

    @Test
    @Order(30)
    void given_plan_should_add_plan_to_config_file() {

        pricingService.addPlanToConfiguration(TEST_NEW_PLAN, newPlan);

        // FIXME
        // READS OLD VALUE
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(TEMPORAL_CONFIG_PATH);

        assertTrue(pricingManager.getPlans().containsKey(TEST_NEW_PLAN),
                "Pricing config does not have NEW_PLAN");

    }

    @Test
    @Order(40)
    void given_duplicate_plan_name_should_throw_exception_when_adding_plan() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.addPlanToConfiguration(TEST_PLAN, newPlan);
        });

        assertEquals("The plan " + TEST_PLAN + " already exists in the current pricing configuration",
                exception.getMessage());

    }

    // // --------------------------- PLAN REMOVAL ---------------------------

    @Test
    @Order(50)
    void given_existing_plan_name_should_delete_plan_from_config() {

        String plan = "BASIC";

        pricingService.removePlanFromConfiguration(plan);

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(TEMPORAL_CONFIG_PATH);

        assertFalse(pricingManager.getPlans().containsKey(plan), "Basic plan was not removed");

    }

    @Test
    @Order(60)
    void given_non_existing_plan_name_should_throw_when_deleting() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.removePlanFromConfiguration(TEST_NEW_PLAN);
        });

        assertEquals("There is no plan with the name " + TEST_NEW_PLAN + " in the current pricing configuration",
                exception.getMessage());

    }

    // // --------------------------- BOOLEAN EDITIONS ---------------------------

    @Test
    @Order(70)
    void given__existing_plan_name_and_feature_should_update_boolean_feature() {

        pricingService.setPlanFeatureValue(TEST_PLAN, TEST_BOOLEAN_ATTRIBUTE,
                true);

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContextTestImpl.getConfigFilePath());

        assertEquals(true,
                pricingManager.getPlans().get(TEST_PLAN).getFeatures().get(TEST_BOOLEAN_ATTRIBUTE).getValue(),
                "haveCalendar from plan BASIC should be true");

    }

    @Test
    @Order(80)
    void given_existing_plan_and_boolean_feature_should_throw_assigning_numeric() {

        Integer newValue = 3;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.setPlanFeatureValue(TEST_PLAN, TEST_BOOLEAN_ATTRIBUTE,
                    newValue);
        });

        assertEquals("The value " + newValue + " is not of the type BOOLEAN", exception.getMessage());

    }

    @Test
    @Order(90)
    void given_existing_plan_and_boolean_feature_should_throw_assigning_null() {

        Boolean newValue = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.setPlanFeatureValue(TEST_PLAN, TEST_BOOLEAN_ATTRIBUTE,
                    newValue);
        });

        assertEquals("The value " + newValue + " is not of the type BOOLEAN",
                exception.getMessage());

    }

    // // --------------------------- NUMERIC EDITIONS ---------------------------

    @Test
    @Order(100)
    void given_existing_plan_and_numeric_feature_should_update() {

        Integer newValue = 6;

        pricingService.setPlanFeatureValue(TEST_PLAN, TEST_NUMERIC_ATTRIBUTE,
                newValue);

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContextTestImpl.getConfigFilePath());

        assertEquals(newValue,
                pricingManager.getPlans().get(TEST_PLAN).getFeatures().get(TEST_NUMERIC_ATTRIBUTE).getValue());

    }

    @Test
    @Order(110)
    void given_string_should_throw_when_updating_numeric_feature() {

        String newValue = "invalidValue";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.setPlanFeatureValue(TEST_PLAN, TEST_NUMERIC_ATTRIBUTE,
                    newValue);
        });

        assertEquals("The value " + newValue + " is not of the type NUMERIC", exception.getMessage());

    }

    @Test
    @Order(120)
    void given_null_should_throw_when_updating_numeric_feature() {

        Integer newValue = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.setPlanFeatureValue(TEST_PLAN, TEST_NUMERIC_ATTRIBUTE,
                    newValue);
        });

        assertEquals("The value " + newValue + " is not of the type NUMERIC",
                exception.getMessage());

    }

    // // --------------------------- TEXT EDITIONS ---------------------------

    @Test
    @Order(130)
    void given_string_should_update_text_feature() {

        String newValue = "HIGH";

        pricingService.setPlanFeatureValue(TEST_PLAN, TEST_TEXT_ATTRIBUTE, newValue);

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContextTestImpl.getConfigFilePath());

        assertEquals(newValue,
                pricingManager.getPlans().get(TEST_PLAN).getFeatures().get(TEST_TEXT_ATTRIBUTE).getValue());

    }

    @Test
    @Order(140)
    void given_number_should_throw_when_updating_text_feature() {

        Integer newValue = 2;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.setPlanFeatureValue(TEST_PLAN, TEST_TEXT_ATTRIBUTE, newValue);
        });

        assertEquals("The value " + newValue + " is not of the type TEXT",
                exception.getMessage());

    }

    @Test
    @Order(150)
    void given_null_should_throw_when_updating_text_feature() {

        String newValue = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.setPlanFeatureValue(TEST_PLAN, TEST_TEXT_ATTRIBUTE, newValue);
        });

        assertEquals("The value " + newValue + " is not of the type TEXT",
                exception.getMessage());

    }

    // ---------------EDITIONS OF NONEXISTENT ATTRIBUTES ------------

    @Test
    @Order(160)
    void given_non_existent_feature_should_throw_IllegalArgumentException() {

        String unexistentAttribute = "unexistentAttribute";
        Integer newValue = 3;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.setPlanFeatureValue(TEST_PLAN, unexistentAttribute, newValue);
        });

        assertEquals("The plan " + TEST_PLAN + " does not have the feature " +
                unexistentAttribute, exception.getMessage());

    }

    // // --------------------------- FEATURES ADITION ---------------------------

    @Test
    @Order(170)
    void given_new_feature_should_update_all_plan_values() {

        Domain newFeature = new Domain();
        newFeature.setName(NEW_FEATURE_NAME);
        newFeature.setDefaultValue(NEW_FEATURE_TEST_VALUE);
        newFeature.setValueType(ValueType.TEXT);
        newFeature.setExpression(NEW_FEATURE_TEST_EXPRESSION);

        pricingService.addFeatureToConfiguration(NEW_FEATURE_NAME, newFeature);

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContextTestImpl.getConfigFilePath());

        assertTrue(pricingManager.getFeatures().containsKey(NEW_FEATURE_NAME));
        assertTrue(pricingManager.getPlans().get("BASIC").getFeatures().containsKey(NEW_FEATURE_NAME));
        assertTrue(pricingManager.getPlans().get("ADVANCED").getFeatures().containsKey(NEW_FEATURE_NAME));
        assertTrue(pricingManager.getPlans().get("PRO").getFeatures().containsKey(NEW_FEATURE_NAME));

        assertEquals(NEW_FEATURE_TEST_EXPRESSION,
                pricingManager.getFeatures().get(NEW_FEATURE_NAME).getExpression());
        assertEquals(NEW_FEATURE_TEST_VALUE,
                pricingManager.getPlans().get("BASIC").getFeatures().get(NEW_FEATURE_NAME).getValue());
        assertEquals(NEW_FEATURE_TEST_VALUE,
                pricingManager.getPlans().get("ADVANCED").getFeatures().get(NEW_FEATURE_NAME).getValue());
        assertEquals(NEW_FEATURE_TEST_VALUE,
                pricingManager.getPlans().get("PRO").getFeatures().get(NEW_FEATURE_NAME).getValue());
    }

    @Test
    @Order(180)
    void given_non_existent_feature_should_throw_when_adding_feature() {

        Domain newFeature = new Domain();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.addFeatureToConfiguration("haveCalendar", newFeature);
        });

        assertEquals(
                "The feature haveCalendar does already exist in the current pricing configuration. Check the features",
                exception.getMessage());
    }

    // // --------------------------- FEATURES REMOVAL ---------------------------

    @Test
    @Order(190)
    void given_existent_feature_should_remove_feature() {

        String featureName = "maxPets";

        pricingService.removeFeatureFromConfiguration("maxPets");

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContextTestImpl.getConfigFilePath());

        assertFalse(pricingManager.getFeatures().containsKey(featureName));
        assertFalse(pricingManager.getPlans().get("BASIC").getFeatures().containsKey(featureName));
        assertFalse(pricingManager.getPlans().get("ADVANCED").getFeatures().containsKey(featureName));
        assertFalse(pricingManager.getPlans().get("PRO").getFeatures().containsKey(featureName));
    }

    @Test
    @Order(200)
    void given_non_existent_feature_should_throw_when_deleting() {

        String nonExistentFeatureName = "foo";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.removeFeatureFromConfiguration(nonExistentFeatureName);
        });

        assertEquals(
                "There is no feature with the name " + nonExistentFeatureName + " in the current pricing configuration",
                exception.getMessage());
    }

    // ------ FEATURES' EXPRESSIONS MANAGEMENT -------

    @Test
    @Order(210)
    void given_feature_update_expression() {

        pricingService.setFeatureExpression(TEST_NUMERIC_ATTRIBUTE,
                NEW_FEATURE_TEST_EXPRESSION);

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContextTestImpl.getConfigFilePath());

        assertEquals(NEW_FEATURE_TEST_EXPRESSION,
                pricingManager.getFeatures().get(TEST_NUMERIC_ATTRIBUTE).getExpression());
    }

    @Test
    @Order(220)
    void given_non_existent_feature_should_throw_when_updating_expression() {

        String nonExistentFeature = "non-existent";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.setFeatureExpression(nonExistentFeature, NEW_FEATURE_TEST_EXPRESSION);
        });

        assertEquals(
                "There is no feature with the name " + nonExistentFeature + " in the current pricing configuration",
                exception.getMessage());
    }

    // ------------ FEATURES' TYPES MANAGEMENT ---------------------

    @Test
    @Order(230)
    void given_feature_should_update_value_type_to_text() {

        pricingService.setValueType("maxPets", ValueType.TEXT);

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContextTestImpl.getConfigFilePath());
        Feature feature = pricingManager.getFeatures().get("maxPets");

        assertEquals(ValueType.TEXT, feature.getValueType());
        assertEquals("", feature.getDefaultValue());
        assertEquals("", feature.getExpression());
        assertEquals("", feature.getServerExpression());

    }

    @Test
    @Order(240)
    void given_feature_should_update_value_type_to_boolean() {

        pricingService.setValueType("maxPets", ValueType.BOOLEAN);

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContextTestImpl.getConfigFilePath());
        Feature feature = pricingManager.getFeatures().get("maxPets");

        assertEquals(ValueType.BOOLEAN, feature.getValueType());
        assertEquals(false, feature.getDefaultValue());
        assertEquals("", feature.getExpression());
        assertEquals("", feature.getServerExpression());

    }

    @Test
    @Order(250)
    void given_non_existent_feature_should_throw_when_updating_value_type() {

        String nonExistentFeature = "non-existent";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.setValueType(nonExistentFeature, ValueType.TEXT);
        });

        assertEquals(
                "There is no feature with the name " + nonExistentFeature + " in the current pricing configuration",
                exception.getMessage());
    }

    // --------------- PLANS' PRICES MANAGEMENT ----------------

    @Test
    @Order(260)
    void given_price_should_update_monthly_price() {

        pricingService.setPlanPrice(TEST_PLAN, 1000.0);

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(pricingContextTestImpl.getConfigFilePath());

        assertEquals(1000.0, pricingManager.getPlans().get(TEST_PLAN).getMonthlyPrice());
    }

    @Test
    @Order(270)
    void negativeChangePlanPriceTest() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pricingService.setPlanPrice(TEST_NEW_PLAN, 1000.0);
        });

        assertEquals("There is no plan with the name " + TEST_NEW_PLAN + " in the current pricing configuration",
                exception.getMessage());
    }

    // ------------- PRICING CONFIGURATION MANAGEMENT ---------------------------

    @Test
    @Order(280)
    void changePricingConfigurationTest() {
        assertDoesNotThrow(() -> {
            pricingService.setPricingConfiguration(ORIGINAL_PRICING_MANAGER);
        });
    }

}
