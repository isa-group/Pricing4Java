package io.github.isagroup.parsing.positive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.isagroup.exceptions.PricingParsingException;
import io.github.isagroup.models.Feature;
import io.github.isagroup.models.Plan;
import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.updaters.Version;
import io.github.isagroup.services.yaml.YamlUtils;

class PricingManagerParserTest {

    private final static String TEST_CASES = "parsing/positive/";

    @Test
    @DisplayName(value = "Given a map of billings should parse it")
    void givenAMapOfBillingShouldParseIt() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(TEST_CASES + "billing/multi-billing.yml");

        assertEquals(3, pricingManager.getBilling().size());
        assertEquals(1.0, pricingManager.getBilling().get("monthly"));
        assertEquals(0.9, pricingManager.getBilling().get("semester"));
        assertEquals(0.83, pricingManager.getBilling().get("annual"));
    }

    @Test
    @DisplayName(value = "Given no 'billing' by default should set a 'monthly: 1'")
    @Disabled
    void givenNoBillingADefaultMonthlyValueShouldBeSet() {

        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(TEST_CASES + "billing/no-billing-set.yml");

        assertEquals(1, pricingManager.getBilling().size());
        assertEquals(1.0, pricingManager.getBilling().get("monthly"));
    }

    @Test
    @DisplayName(value = "Given a single entry in 'billing' map should parse it")
    @Disabled
    void givenBillingOneEntryShouldParse() {

        PricingManager pricingManager = YamlUtils
                .retrieveManagerFromYaml(TEST_CASES + "billing/single-entry-billing.yml");

        assertEquals(1, pricingManager.getBilling().size());
        assertEquals(1.0, pricingManager.getBilling().get("monthly"));
    }

    @Test
    @DisplayName(value = "A date is valid in 'createdAt' and should parse it")
    void givenYamlDateShouldParseCorrectly() {

        PricingManager pricingManager = YamlUtils
                .retrieveManagerFromYaml(TEST_CASES + "createdAt/pricing-date-createdAt.yml");

        assertEquals(LocalDate.of(2024, 1, 15), pricingManager.getCreatedAt());
    }

    @Test
    @DisplayName(value = "A string like yyyy-mm-dd is valid in 'createdAt' and should parse it")
    void givenYamlStrigifyDateShouldParseCorrectly() {

        PricingManager pricingManager = YamlUtils
                .retrieveManagerFromYaml(TEST_CASES + "createdAt/pricing-string-createdAt.yml");

        assertEquals(LocalDate.of(2024, 1, 15), pricingManager.getCreatedAt());
    }

    @Test
    @DisplayName(value = "Pricing should have at least 3 characters")
    void givenSaasNameVerifyLowerLimit() {

        PricingManager pricingManager = YamlUtils
                .retrieveManagerFromYaml(TEST_CASES + "saasName/pricing-lower-limit-name.yml");

        assertEquals(3, pricingManager.getSaasName().length());
    }

    @Test
    @DisplayName(value = "Pricing should have at most 50 characters")
    void givenSaasNameVerifyUpperLimit() {

        PricingManager pricingManager = YamlUtils
                .retrieveManagerFromYaml(TEST_CASES + "saasName/pricing-top-limit-name.yml");

        assertEquals(50, pricingManager.getSaasName().length());
    }

    @Test
    @DisplayName(value = "Pricing can have several 'tags' to split features")
    void givenTagsShouldParseThem() {

        PricingManager pricingManager = YamlUtils
                .retrieveManagerFromYaml(TEST_CASES + "tags/pricing-tags.yml");

        assertEquals(3, pricingManager.getTags().size());
        assertEquals("Tag 1", pricingManager.getTags().get(0));
        assertEquals("Tag 2", pricingManager.getTags().get(1));
        assertEquals("Tag 3", pricingManager.getTags().get(2));

        for (Feature feat : pricingManager.getFeatures().values()) {
            if (feat.getTag() != null) {
                assertTrue(pricingManager.getTags().contains(feat.getTag()),
                        "Feature " + feat.getName() + " not contained in 'tags'");
            }
        }

    }

    @Test
    @DisplayName(value = "Given a 'url' with http protocol is ok")
    void givenHttpUrlShouldParse() {

        PricingManager pricingManager = YamlUtils
                .retrieveManagerFromYaml(TEST_CASES + "url/http-url.yml");

        assertNotNull(pricingManager.getUrl());
        assertTrue(pricingManager.getUrl().startsWith("http://"));
    }

    @Test
    @DisplayName(value = "Given a 'url' with https protocol is ok")
    void givenHttpsUrlShouldParse() {

        PricingManager pricingManager = YamlUtils
                .retrieveManagerFromYaml(TEST_CASES + "url/https-url.yml");

        assertNotNull(pricingManager.getUrl());
        assertTrue(pricingManager.getUrl().startsWith("https://"));
    }

    @Test
    @DisplayName(value = "Given a 'url' with https protocol is ok")
    void givenANullUrlShouldParse() {

        PricingManager pricingManager = YamlUtils
                .retrieveManagerFromYaml(TEST_CASES + "url/no-url.yml");

        assertNull(pricingManager.getUrl());
    }

    @Test
    @DisplayName(value = "Given a plan price ecuation with 'variables' should compute result")
    void givenAMapOfVariablesAndAPriceExpressionShouldComputeResult() {

        PricingManager pm = YamlUtils.retrieveManagerFromYaml(TEST_CASES + "variables/pricing-with-variables.yml");
        assertNotNull(pm.getPlans().get("BASIC").getPrice());
        assertEquals(0.0, pm.getPlans().get("BASIC").getPrice());
        assertNotNull(pm.getPlans().get("PRO").getPrice());
        assertEquals(15.99, pm.getPlans().get("PRO").getPrice());
    }

    @ParameterizedTest
    @ValueSource(strings = { "version-as-string", "version-as-float" })
    void givenVersionShouldParse(String input) {

        String path = String.format(TEST_CASES + "version/%s.yml", input);
        PricingManager pricingManager = YamlUtils.retrieveManagerFromYaml(path);
        assertInstanceOf(Version.class, pricingManager.getVersion());
    }
}
