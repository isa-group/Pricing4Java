package io.github.isagroup.parsing.positive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.isagroup.models.PricingManager;
import io.github.isagroup.services.yaml.YamlUtils;

public class FeatureParserTest {

        private static final String POSITIVE_CASES = "parsing/positive/feature/";

        @Test
        @DisplayName(value = "Given a list of payment methods in 'defaultValue' should parse it")
        void givenPaymentFeatureDefaultValueShouldBeListOfPaymentMethods() {

                List<String> expectedPaymentMethods = new ArrayList<>();
                expectedPaymentMethods.add("CARD");
                String paymentFeatName = "payment";

                PricingManager pricingManager = YamlUtils
                                .retrieveManagerFromYaml(POSITIVE_CASES + "type/payment-feature.yml");

                assertNotNull(pricingManager.getFeatures().get(paymentFeatName));
                List<String> actualPaymentMethods = (List<String>) pricingManager.getFeatures().get(paymentFeatName)
                                .getDefaultValue();
                assertEquals(expectedPaymentMethods, actualPaymentMethods,
                                "Payment methods should be a list of payment methods");

        }

        @Test
        @DisplayName(value = "A feature 'expression' should be optional")
        void givenAFeatureExpressionShouldBeOptional() {

                String featName = "foo";
                PricingManager pricingManager = YamlUtils
                                .retrieveManagerFromYaml(POSITIVE_CASES + "expression/expression-is-null.yml");

                assertNotNull(pricingManager.getFeatures().get(featName));
                assertNull(pricingManager.getFeatures().get(featName).getExpression());

        }
}
