package io.github.isagroup;

import io.github.isagroup.exceptions.CloneUsageLimitException;
import io.github.isagroup.exceptions.InvalidDefaultValueException;
import io.github.isagroup.models.*;
import io.github.isagroup.models.featuretypes.Information;
import io.github.isagroup.models.usagelimittypes.NonRenewable;
import io.github.isagroup.models.usagelimittypes.Renewable;
import io.github.isagroup.services.yaml.YamlUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.AnnotatedElementContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.io.TempDirFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PricingServiceTests {

    @TempDir(factory = Factory.class)
    private Path tempDir;

    private PricingService pricingService;
    private PricingContextTestImpl pricingConfig;

    private static PricingManager petClinic, postMan, terminator;
    private static UsageLimit newUsageLimit;
    private static Plan newPlan;
    private static AddOn newAddOn;

    @Test
    void factoryTest() {
        assertTrue(tempDir.getFileName().toString().startsWith("yaml-testing"));
    }

    static class Factory implements TempDirFactory {

        @Override
        public Path createTempDirectory(AnnotatedElementContext elementContext, ExtensionContext extensionContext)
                throws IOException {

            File file = new File("src/main/resources/yaml-testing");

            if (file.exists() && file.isDirectory()) {
                return Path.of("src", "main", "resources", "yaml-testing");
            }

            return Files.createDirectory(Path.of("src", "main", "resources", "yaml-testing"));
        }

    }

    private String getTempPricingPath(String yamlName) {
        return this.tempDir.getFileName() + "/" + yamlName + ".yml";
    }

    @BeforeAll
    static void beforeAll() {

        petClinic = YamlUtils.retrieveManagerFromYaml("pricing/petclinic.yml");
        postMan = YamlUtils.retrieveManagerFromYaml("pricing/postman.yml");
        terminator = YamlUtils.retrieveManagerFromYaml("pricing/terminator.yml");

        newUsageLimit = new Renewable();
        newUsageLimit.setName("maxAppointments");
        newUsageLimit.setDescription("New usage limit description");
        newUsageLimit.setValueType(ValueType.NUMERIC);
        newUsageLimit.setDefaultValue(10);
        newUsageLimit.setUnit("appointment");
        newUsageLimit.getLinkedFeatures().add("haveOnlineConsultation");

        newPlan = new Plan();
        newPlan.setName("NEW_PLAN");
        newPlan.setDescription("New plan description");
        newPlan.setPrice(0.0);
        newPlan.setUnit("clinic/month");

        newAddOn = new AddOn();
        newAddOn.setName("newAddOn");
        newAddOn.setPrice(5.0);
        newAddOn.setUnit("owner/month");
        List<String> availableFor = new ArrayList<>();
        availableFor.add("BASIC");
        newAddOn.setAvailableFor(availableFor);

        Feature newAddOnFeature = petClinic.getFeatures().get("haveVetSelection");
        Map<String, Feature> addOnFeatures = new HashMap<>();
        addOnFeatures.put("haveVetSelection", newAddOnFeature);
        newAddOn.setFeatures(addOnFeatures);

        UsageLimit addOnUsageLimitExtension = new NonRenewable();
        addOnUsageLimitExtension.setName("maxPets");
        addOnUsageLimitExtension.setDescription("Max pets extension description");
        addOnUsageLimitExtension.setValueType(ValueType.NUMERIC);
        addOnUsageLimitExtension.setDefaultValue(5);
        addOnUsageLimitExtension.setUnit("pet");
        addOnUsageLimitExtension.getLinkedFeatures().add("maxPets");
        Map<String, UsageLimit> usageLimitsExtensions = new HashMap<>();
        usageLimitsExtensions.put(addOnUsageLimitExtension.getName(), addOnUsageLimitExtension);
        newAddOn.setUsageLimitsExtensions(usageLimitsExtensions);
    }

    @BeforeEach
    void setUp() {
        this.pricingConfig = new PricingContextTestImpl();
        this.pricingService = new PricingService(pricingConfig);
    }

    @Test
    @Disabled
    void givenPricingShouldDumpACopy() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("postman"));
        pricingConfig.setConfigFilePath(getTempPricingPath("postman"));

        assertEquals(postMan, pricingConfig.getPricingManager(), "Pricings are diferent");

    }

    // --------------------------- PLAN RETRIEVAL ---------------------------

    @Test
    void givenAPlanNameServiceShouldReturnPlan() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));

        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        Plan plan = pricingService.getPlanFromName("BASIC");

        assertEquals(petClinic.getPlans().get("BASIC"), plan);
        assertEquals(0.0, plan.getPrice());

    }

    @Test
    void givenNonExistentPlanShouldThrow() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));

        String nonExistentPlan = "nonExistentPlan";
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        try {
            pricingService.getPlanFromName(nonExistentPlan);
        } catch (IllegalArgumentException e) {
            assertEquals("The plan " + nonExistentPlan + " does not exist in the current pricing configuration",
                    e.getMessage());
        }
    }

    // --------------------------- PLAN ADITION ---------------------------

    @Test
    void givenPlanShouldAddPlanToConfigFile() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        pricingService.addPlanToConfiguration(newPlan);

        assertTrue(pricingService.getPricingPlans().containsKey(newPlan.getName()),
                "Pricing config does not have " + newPlan.getName());

    }

    @Test
    void givenDuplicatePlanShouldThrowExceptionWhenAddingPlan() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        try {
            pricingService.addPlanToConfiguration(newPlan);
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        }

        try {
            pricingService.addPlanToConfiguration(newPlan);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("The plan " + newPlan.getName() + " already exists in the current pricing configuration",
                    e.getMessage());
        }
    }

    // --------------------------- PLAN UPDATE ---------------------------

    @Test
    void givenNewPlanNameShouldUpdateOnlyPlanName() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        Plan previousPlan = pricingConfig.getPricingManager().getPlans().get("BASIC");

        String newPlanName = "NEW_NAME";
        Plan newPlan = pricingConfig.getPricingManager().getPlans().get("BASIC");
        newPlan.setName(newPlanName);
        newPlan.setPrice(20.99);

        pricingService.updatePlanFromConfiguration(previousPlan.getName(), newPlan);

        assertFalse(pricingConfig.getPricingManager().getPlans().containsKey(previousPlan.getName()));
        assertTrue(pricingConfig.getPricingManager().getPlans().containsKey(newPlanName));
        assertEquals(20.99, pricingConfig.getPricingManager().getPlans().get(newPlanName).getPrice());
    }

    @Test
    void givenNonExistentPlanShouldThrowWhenUpdatingIt() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        try {
            pricingService.updatePlanFromConfiguration("nonExistentFeature", newPlan);
        } catch (IllegalArgumentException e) {
            assertEquals("There is no plan with the name nonExistentFeature in the current pricing configuration",
                    e.getMessage());
        }
    }

    @Test
    void givenNullPlanShouldThrowIllegalArgumentExceptionException() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        try {
            pricingService.updatePlanFromConfiguration("BASIC", null);
        } catch (IllegalArgumentException e) {
            assertEquals("A null plan cannot be added to the pricing configuration", e.getMessage());
        }
    }

    @Test
    void givenAPlanWithNegativePriceShouldThrow() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        Plan plan = pricingConfig.getPricingManager().getPlans().get("BASIC");
        plan.setPrice(-1.0);

        try {
            pricingService.updatePlanFromConfiguration("BASIC", plan);
        } catch (IllegalArgumentException e) {
            assertEquals("", e.getMessage());
        }
    }

    @Test
    void givenNullNameShouldWheAddingPlanShouldThrow() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        try {
            pricingService.updatePlanFromConfiguration(null, newPlan);
        } catch (IllegalArgumentException e) {
            assertEquals("There is no plan with the name null in the current pricing configuration", e.getMessage());
        }

    }

    // --------------------------- PLAN REMOVAL ---------------------------

    @Test
    void givenExistingPlanNameShouldDeletePlanFromConfig() {

        String planName = "BASIC";
        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        pricingService.removePlanFromConfiguration(planName);

        assertFalse(pricingConfig.getPricingManager().getPlans().containsKey(planName), "Basic plan was not removed");

    }

    @Test
    void givenNonExistingPlanNameShouldThrowWhenDeleting() {

        String planName = "NonExistentPlan";
        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        try {
            pricingService.removePlanFromConfiguration(planName);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("There is no plan with the name " + planName + " in the current pricing configuration",
                    e.getMessage());
        }
    }

    @Test
    void givenExistingUsageLimitShouldUpdateSameUsageLimit() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        assertEquals(10, pricingConfig.getPricingManager().getUsageLimits().get("maxPets").getDefaultValue());
        UsageLimit newUsageLimit = pricingConfig.getPricingManager().getUsageLimits().get("maxPets");
        newUsageLimit.setDefaultValue(20);

        pricingService.updateUsageLimitFromConfiguration("maxPets", newUsageLimit);
        assertEquals(20, pricingConfig.getPricingManager().getUsageLimits().get("maxPets").getDefaultValue());

    }

    // --------------------------- FEATURE ADITION ---------------------------

    @Test
    void givenNewFeatureShouldAppearInAllPlans() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        Information businessAnalysis = new Information();
        String featureName = "businessAnalysis";
        businessAnalysis.setName(featureName);
        businessAnalysis.setDescription("In depth views for you business");
        businessAnalysis.setValueType(ValueType.BOOLEAN);
        businessAnalysis.setDefaultValue(false);
        businessAnalysis.setExpression("planContext['features']['businessAnalysis']");
        businessAnalysis.setServerExpression("");

        pricingService.addFeatureToConfiguration(businessAnalysis);

        Map<String, Plan> plans = pricingConfig.getPricingManager().getPlans();
        for (Plan plan : plans.values()) {
            assertEquals(businessAnalysis, plan.getFeatures().get(featureName));
        }
    }

    @Test
    void givenExistentFeatureShouldThrowWhenAddingFeature() {

        String featureName = "maxPets";
        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        try {
            pricingService.addFeatureToConfiguration(pricingConfig.getPricingManager().getFeatures().get(featureName));
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "The feature " + featureName
                            + " does already exist in the current pricing configuration. Check the features",
                    e.getMessage());
        }
    }

    @Test
    void givenDuplicateFeatureWhenAddingFeatureShouldThrow() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        Information businessAnalysis = new Information();
        String featureName = "businessAnalysis";
        businessAnalysis.setName(featureName);
        businessAnalysis.setDescription("In depth views for you business");
        businessAnalysis.setValueType(ValueType.BOOLEAN);
        businessAnalysis.setDefaultValue(false);
        businessAnalysis.setExpression("planContext['features']['businessAnalysis']");
        businessAnalysis.setServerExpression("");

        pricingService.addFeatureToConfiguration(businessAnalysis);
        try {
            pricingService.addFeatureToConfiguration(businessAnalysis);
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "The feature businessAnalysis does already exist in the current pricing configuration. Check the features",
                    e.getMessage());
        }
    }

    // --------------------------- FEATURES REMOVAL ---------------------------

    @Test
    void givenExistentFeatureShouldRemoveFromConfiguration() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        String featureName = "maxPets";

        pricingService.removeFeatureFromConfiguration("maxPets");

        assertFalse(pricingConfig.getPricingManager().getFeatures().containsKey(featureName));
        assertFalse(pricingConfig.getPricingManager().getPlans().get("BASIC").getFeatures().containsKey(featureName));
        assertFalse(
                pricingConfig.getPricingManager().getPlans().get("ADVANCED").getFeatures().containsKey(featureName));
        assertFalse(pricingConfig.getPricingManager().getPlans().get("PRO").getFeatures().containsKey(featureName));
    }

    @Test
    void givenNullFeatureShouldThroWhenDeleting() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        try {
            pricingService.removeFeatureFromConfiguration(null);
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "There is no feature with the name " + null + " in the current pricing configuration",
                    e.getMessage());
        }
    }

    @Test
    void givenAFeatureShouldRemoveItPlans() {

        YamlUtils.writeYaml(terminator, getTempPricingPath("terminator"));
        pricingConfig.setConfigFilePath(getTempPricingPath("terminator"));
        String skynet = "skynet";
        pricingService.removeFeatureFromConfiguration(skynet);

        PricingManager terminator = pricingConfig.getPricingManager();
        Map<String, Feature> features = terminator.getFeatures();
        Map<String, Plan> plans = terminator.getPlans();

        assertFalse(features.containsKey(skynet));

        for (Plan plan : plans.values()) {
            assertFalse(plan.getFeatures().containsKey(skynet));
        }
    }

    @Test
    void givenNonExistentFeatureShouldThrowWhenDeleting() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        String nonExistentFeatureName = "nonExistentFeature";

        try {
            pricingService.removeFeatureFromConfiguration(nonExistentFeatureName);
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "There is no feature with the name " + nonExistentFeatureName
                            + " in the current pricing configuration",
                    e.getMessage());
        }
    }

    // --------------- USAGE LIMITS' MANAGEMENT ----------------

    @Test
    void givenNewUsageLimitShouldUpdateConfiguration() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        pricingService.addUsageLimitToConfiguration(newUsageLimit);

        UsageLimit actualUsageLimit = pricingConfig.getPricingManager().getUsageLimits().get(newUsageLimit.getName());

        assertEquals("New usage limit description", actualUsageLimit.getDescription());
        assertEquals(ValueType.NUMERIC, actualUsageLimit.getValueType());
        assertEquals(10, actualUsageLimit.getDefaultValue());
        assertEquals("appointment", actualUsageLimit.getUnit());
        assertTrue(actualUsageLimit.getLinkedFeatures().contains("haveOnlineConsultation"));
    }

    @Test
    void givenExistingUsageLimitShouldThrowCloneUsageLimitException() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));

        String usageLimitName = "maxPets";
        try {
            pricingService.addUsageLimitToConfiguration(
                    pricingConfig.getPricingManager().getUsageLimits().get(usageLimitName));
        } catch (CloneUsageLimitException e) {
            assertEquals("An usage limit with the name " + usageLimitName
                    + " already exists within the pricing configuration", e.getMessage());
        }

    }

    @Test
    void givenUsageLimitShouldUpdate() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        pricingService.addUsageLimitToConfiguration(newUsageLimit);

        UsageLimit updatedUsageLimit = pricingConfig.getPricingManager().getUsageLimits().get(newUsageLimit.getName());
        updatedUsageLimit.setDefaultValue(20);
        updatedUsageLimit.setUnit("day");
        updatedUsageLimit.getLinkedFeatures().add("supportPriority");

        pricingService.updateUsageLimitFromConfiguration(newUsageLimit.getName(), updatedUsageLimit);

        assertEquals("New usage limit description", updatedUsageLimit.getDescription());
        assertEquals(ValueType.NUMERIC, updatedUsageLimit.getValueType());
        assertEquals(20, updatedUsageLimit.getDefaultValue());
        assertEquals("day", updatedUsageLimit.getUnit());
        assertTrue(updatedUsageLimit.getLinkedFeatures().contains("supportPriority"));
    }

    @Test
    @Order(290)
    void negativeShouldUpdateUsageLimit() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        pricingService.addUsageLimitToConfiguration(newUsageLimit);

        newUsageLimit.setDefaultValue("test");

        String newUsageLimitName = newUsageLimit.getName();

        InvalidDefaultValueException exception = assertThrows(InvalidDefaultValueException.class, () -> {
            pricingService.updateUsageLimitFromConfiguration(newUsageLimitName, newUsageLimit);
        });

        assertEquals(
                "The usage limit " + newUsageLimit.getName()
                        + " defaultValue must be one of the supported numeric types if valueType is NUMERIC",
                exception.getMessage());
    }

    @Test
    void givenAnUsageLimitShouldItFromConfiguration() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        pricingService.addUsageLimitToConfiguration(newUsageLimit);

        pricingService.removeUsageLimitFromConfiguration(newUsageLimit.getName());

        assertFalse(pricingConfig.getPricingManager().getUsageLimits().containsKey(newUsageLimit.getName()));
    }

    @Test
    void shoulsfdRemoveUsageLimit() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        pricingService.addUsageLimitToConfiguration(newUsageLimit);

        try {
            pricingService.removeUsageLimitFromConfiguration(null);
        } catch (IllegalArgumentException e) {
            assertEquals("There is no usage limit with the name null in the current pricing configuration",
                    e.getMessage());
        }
    }

    // --------------- ADD-ONS' MANAGEMENT ----------------

    @Test
    void givenANewAddOnShouldAddToConfiguration() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        assertNull(pricingConfig.getPricingManager().getAddOns());

        pricingService.addAddOnToConfiguration(newAddOn);

        assertEquals(1, pricingConfig.getPricingManager().getAddOns().size());
        assertEquals(newAddOn.getName(),
                pricingConfig.getPricingManager().getAddOns().get(newAddOn.getName()).getName());
    }

    @Test
    void shouldNotAddRepeatedAddOn() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        assertNull(pricingConfig.getPricingManager().getAddOns());

        pricingService.addAddOnToConfiguration(newAddOn);

        assertEquals(1, pricingConfig.getPricingManager().getAddOns().size());
        assertEquals(newAddOn.getName(),
                pricingConfig.getPricingManager().getAddOns().get(newAddOn.getName()).getName());

        try {
            pricingService.addAddOnToConfiguration(newAddOn);
        } catch (IllegalArgumentException e) {
            assertEquals("An add-on with the name " + newAddOn.getName()
                    + " already exists within the pricing configuration", e.getMessage());
        }
    }

    @Test
    void shouldUpdateAddOn() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        assertNull(pricingConfig.getPricingManager().getAddOns());

        pricingService.addAddOnToConfiguration(newAddOn);

        AddOn updatedAddOn = newAddOn;
        updatedAddOn.setPrice(10.0);
        updatedAddOn.setUnit("owner/year");

        pricingService.updateAddOnFromConfiguration(newAddOn.getName(), updatedAddOn);

        assertEquals(10.0, pricingConfig.getPricingManager().getAddOns().get(newAddOn.getName()).getPrice());
        assertEquals("owner/year", pricingConfig.getPricingManager().getAddOns().get(newAddOn.getName()).getUnit());
    }

    @Test
    void givenPlanNameShouldDeleteAddOn() {

        YamlUtils.writeYaml(petClinic, getTempPricingPath("petclinic"));
        pricingConfig.setConfigFilePath(getTempPricingPath("petclinic"));
        pricingService.addAddOnToConfiguration(newAddOn);

        pricingService.removeAddOnFromConfiguration(newAddOn.getName());

        assertFalse(pricingConfig.getPricingManager().getAddOns().containsKey(newAddOn.getName()));
    }

}
