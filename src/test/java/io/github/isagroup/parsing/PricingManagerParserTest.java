package io.github.isagroup.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.isagroup.exceptions.FilepathException;
import io.github.isagroup.exceptions.InvalidPlanException;
import io.github.isagroup.exceptions.PricingParsingException;
import io.github.isagroup.exceptions.VersionException;
import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.updaters.Version;
import io.github.isagroup.services.yaml.YamlUtils;

class PricingManagerParserTest {

    @Test
    void givenPetclinicShouldGetPricingManager() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml("pricing/petclinic.yml");

        assertTrue(pricingManager.getPlans().get("BASIC") instanceof Plan,
                "Should be an instance of PricingManager");
        assertEquals(false,
                pricingManager.getPlans().get("BASIC").getFeatures().get("haveCalendar")
                        .getDefaultValue(),
                "The deafult value of the haveCalendar feature should be false");
        assertEquals(null, pricingManager.getPlans().get("BASIC").getFeatures().get("maxPets").getValue(),
                "The value of the maxPets should be null");

    }

    @ParameterizedTest
    @ValueSource(strings = { "version-as-string", "version-as-float" })
    void givenDifferentFormatsShouldEqualToOneDotZero(String input) {

        String path = String.format("parsing/legacy-tests/pricing-manager/version/positive/%s.yml", input);
        try {
            YamlUtils.retrieveManagerFromYaml(path);
        } catch (PricingParsingException e) {
            fail("file " + input + " " + e.getMessage());
        }
    }

    @Test
    void giveVersionV10ShouldParse() {
        String path = "parsing/legacy-tests/pricing-manager/positive/v1.0.yml";

        try {
            PricingManager pm = YamlUtils.retrieveManagerFromYaml(path);
            assertEquals(Version.V2_0, pm.getVersion());
            assertEquals(LocalDate.of(2024, 8, 31), pm.getCreatedAt());

        } catch (PricingParsingException e) {
            fail(e.getMessage());
        }

    }

    @Test
    void givenAMapOfVariablesAndAPriceExpressionShouldComputeResult() {
        String path = "parsing/legacy-tests/pricing-manager/positive/pricing-with-variables.yml";

        try {
            PricingManager pm = YamlUtils.retrieveManagerFromYaml(path);
            assertNotNull(pm.getPlans().get("BASIC").getPrice());
            assertEquals(0.0, pm.getPlans().get("BASIC").getPrice());
            assertNotNull(pm.getPlans().get("PRO").getPrice());
            assertEquals(15.99, pm.getPlans().get("PRO").getPrice());

        } catch (PricingParsingException e) {
            fail(e.getMessage());
        }

    }

    @Test
    void givenVersionV11ShouldParse() {
        String path = "parsing/legacy-tests/pricing-manager/positive/v1.1.yml";
        try {
            PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(path);
            assertEquals(Version.V2_0, pricingManager.getVersion());
            assertEquals(LocalDate.of(2024, 8, 30), pricingManager.getCreatedAt());
        } catch (PricingParsingException e) {
            fail(e.getMessage());
        }
    }

    @ParameterizedTest(name = "{0} - {1}")
    @CsvFileSource(resources = "/negative-parsing-tests.csv", delimiter = ';')
    void negativeTests(String testDescription, String fileName, String expectedErrorMessage) {

        try {
            YamlUtils.retrieveManagerFromYaml(fileName);
            fail();
        } catch (PricingParsingException | InvalidPlanException | VersionException e) {
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    void givenFeatureWithTagShouldParse() {

        String path = "parsing/legacy-tests/pricing-manager/positive/feature-tag.yml";
        try {
            PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(path);
            assertEquals(2, pricingManager.getFeatures().size());
        } catch (PricingParsingException e) {
            fail(e.getMessage());
        }
    }

}
