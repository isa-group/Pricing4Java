package io.github.isagroup.pricingcontext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.isagroup.PricingContext;
import io.github.isagroup.PricingEvaluatorUtil;
import io.github.isagroup.services.jwt.PricingJwtUtils;

public class PricingConfigExpirationTest {

    private PricingContext pricingContext;

    private PricingEvaluatorUtil pricingEvaluatorUtil;

    private PricingJwtUtils pricingJwtUtils;

    @BeforeEach
    void setup() {
        this.pricingContext = new ShortExpirationConfig();
        this.pricingEvaluatorUtil = new PricingEvaluatorUtil(pricingContext);
        this.pricingJwtUtils = new PricingJwtUtils(pricingContext);
    }

    private class ShortExpirationConfig extends PricingContext {

        @Override
        public String getConfigFilePath() {
            return "yaml-testing/petclinic.yml";
        }

        @Override
        public String getJwtSecret() {
            return "p3tclinic";
        }

        @Override
        public Map<String, Object> getUserContext() {
            return Map.of("pets", 5);
        }

        @Override
        public String getUserPlan() {
            return "BASIC";
        }

        public int getJwtExpiration() {
            return 1000;
        }

    }

    @Test
    void givenShortPricingExpirationTimeShouldExpire() {

        String token = pricingEvaluatorUtil.generateUserToken();

        try {
            Thread.sleep(this.pricingContext.getJwtExpiration());
        } catch (InterruptedException e) {
            fail();
        }
        boolean isPricingValid = pricingJwtUtils.validateJwtToken(token);
        assertFalse(isPricingValid);
    }

}
