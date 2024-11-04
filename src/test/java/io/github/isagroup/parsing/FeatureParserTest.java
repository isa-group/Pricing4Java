package io.github.isagroup.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import io.github.isagroup.exceptions.FilepathException;
import io.github.isagroup.exceptions.PricingParsingException;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.yaml.YamlUtils;

public class FeatureParserTest {

    private static final String TEST_CASES = "parsing/feature/";
    private static final String POSITIVE_CASES = TEST_CASES + "positive/";

    @Test
    void givenPaymentFeatureBasicPlanValueShouldBeAList() {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(POSITIVE_CASES + "payment-feature.yml");

        List<String> overwrittenPaymentMethods = (List<String>) pricingManager.getPlans().get("BASIC").getFeatures()
            .get("payment")
            .getValue();

        assertInstanceOf(List.class, overwrittenPaymentMethods,
            "Payment methods is not a list of payment methods");
        assertEquals(2, overwrittenPaymentMethods.size());

    }

    @Test
    void givenPaymentFeatureDefaultValueShouldBeListOfPaymentMethods() {
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(POSITIVE_CASES + "payment-feature.yml");

        List<String> expectedPaymentMethods = new ArrayList<>();
        expectedPaymentMethods.add("CARD");
        List<String> actualPaymentMethods = (List<String>) pricingManager.getFeatures().get("payment")
            .getDefaultValue();

        assertEquals(expectedPaymentMethods, actualPaymentMethods,
            "Payment methods should be a list of payment methods");

    }


    @Test
    void givenNonNullFeaturesInBasicPlanShouldHaveDefaultValues() {

        PricingManager pricingManager = YamlUtils
            .retrieveManagerFromYaml(POSITIVE_CASES + "non-null-features-basic.yml");

        Boolean value = (Boolean) pricingManager.getPlans().get("BASIC").getFeatures().get("featureA")
            .getValue();

        assertEquals(true, value);
    }

    @Test
    void givenNullFeaturesBasicPlanShouldHaveDefaultValues() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(POSITIVE_CASES + "null-features-basic.yml");

        Boolean value = (Boolean) pricingManager.getPlans().get("BASIC").getFeatures().get("featureA")
            .getValue();

        assertNull(value, "BASIC plan value is not null");
    }

    @Test
    void givenNullFeaturesShouldParseAListOfDefaultFeatures() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(POSITIVE_CASES + "null-features-basic.yml");

        boolean value = pricingManager.getPlans().get("BASIC").getFeatures().isEmpty();

        assertEquals(false, value, "Basic plan does not have a list of features");
    }


    @ParameterizedTest
    @CsvFileSource(resources = "/feature-negative.csv", delimiter = ';', useHeadersInDisplayName = true)
    void givenNegativeCasesFeaturesShouldThrow(String fileName, String expectedErrorMessage) {

        String path = String.format("parsing/feature/negative/%s.yml", fileName);

        try {
            YamlUtils.retrieveManagerFromYaml(path);
            fail();
        } catch (FilepathException e) {
            System.out.println(path);
            fail(e.getMessage());
        } catch (PricingParsingException e) {
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

}
