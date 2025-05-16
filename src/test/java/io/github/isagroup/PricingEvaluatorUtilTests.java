package io.github.isagroup;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.isagroup.services.jwt.PricingJwtUtils;

import static org.junit.jupiter.api.Assertions.*;

public class PricingEvaluatorUtilTests {

    private PricingContext pricingContext = new PricingContextTestImpl();

    private PricingEvaluatorUtil pricingEvaluatorUtil = new PricingEvaluatorUtil(pricingContext);

    private PricingJwtUtils jwtUtils = new PricingJwtUtils(pricingContext);

    @Test
    void simpleTokenGenerationTest() {

        String token = pricingEvaluatorUtil.generateUserToken();

        Map<String, Map<String, Object>> features = jwtUtils.getFeaturesFromJwtToken(token);

        assertTrue(jwtUtils.validateJwtToken(token), "Token is not valid");
        assertTrue((Boolean) features.get("pets").get("eval"), "Features is not a string");
        assertTrue((Boolean) features.get("visits").get("eval"), "Features is not a string");
        assertTrue((Boolean) features.get("haveCalendar").get("eval"),
                "haveCalendar evaluation should be true");
        assertFalse((Boolean) features.get("haveOnlineConsultation").get("eval"),
                "haveVetSelection evaluation should be false");

    }

    @Test
    void checkTokenSubjectTest() {

        String token = pricingEvaluatorUtil.generateUserToken();

        String jwtSubject = jwtUtils.getSubjectFromJwtToken(token);

        assertTrue(jwtUtils.validateJwtToken(token), "Token is not valid");
        assertEquals(PricingContextTestImpl.JWT_SUBJECT_TEST, jwtSubject, "The subject has not being correctly set");

    }

    @Test
    void tokenExpressionsTest() {

        String firstToken = pricingEvaluatorUtil.generateUserToken();
        String jwtExpressionTest = "userContext['pets']*4 < planContext['usageLimits']['pets']";

        String newToken = pricingEvaluatorUtil.addExpressionToToken(firstToken, "visits",
                jwtExpressionTest);

        Map<String, Map<String, Object>> features = jwtUtils.getFeaturesFromJwtToken(newToken);

        assertTrue(jwtUtils.validateJwtToken(newToken), "Token is not valid");
        assertEquals(jwtExpressionTest, (String) features.get("visits").get("eval"),
                "The expression for the feature visits has not being correctly set");

    }

    @Test
    void tokenPlanContextTest() {

        String token = pricingEvaluatorUtil.generateUserToken();

        Map<String, Object> tokenicedPlanContext = jwtUtils.getPlanContextFromJwtToken(token);

        assertTrue(jwtUtils.validateJwtToken(token), "Token is not valid");
        assertEquals((Integer) pricingContext.getPlanContext().get("pets"),
                (Integer) tokenicedPlanContext.get("pets"),
                "PlanContext pets value is not the same after token codification");
        assertEquals((Boolean) pricingContext.getPlanContext().get("havePetsDashboard"),
                (Boolean) tokenicedPlanContext.get("havePetsDashboard"),
                "PlanContext havePetsDashboard value is not the same after token codification");
        assertEquals((String) pricingContext.getPlanContext().get("supportPriority"),
                (String) tokenicedPlanContext.get("supportPriority"),
                "PlanContext havePetsDashboard value is not the same after token codification");

    }

    @Test
    void tokenUserContextTest() {

        String token = pricingEvaluatorUtil.generateUserToken();

        Map<String, Object> tokenicedUserContext = jwtUtils.getUserContextFromJwtToken(token);

        assertTrue(jwtUtils.validateJwtToken(token), "Token is not valid");
        assertEquals((Integer) pricingContext.getUserContext().get("pets"),
                (Integer) tokenicedUserContext.get("pets"),
                "UserContext pets value is not the same after token codification");
        assertEquals((Boolean) pricingContext.getUserContext().get("havePetsDashboard"),
                (Boolean) tokenicedUserContext.get("havePetsDashboard"),
                "UserContext havePetsDashboard value is not the same after token codification");

    }
}
